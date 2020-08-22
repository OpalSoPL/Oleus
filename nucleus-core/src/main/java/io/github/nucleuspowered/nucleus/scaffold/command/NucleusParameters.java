/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import static io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters.Keys.WORLD;
import static io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters.Keys.XYZ;

import io.github.nucleuspowered.nucleus.scaffold.command.parameter.DisplayNameArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.GameProfileArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.NucleusWorldPropertiesArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.SelectorArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.TimespanArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.UUIDArgument;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

/**
 * A selection of common parameters for making things consistent
 */
public class NucleusParameters {

    public static class Keys {

        private Keys() {}

        public static final String BOOL = "true|false";
        public static final String COMMAND = "command";
        public static final String DESCRIPTION = "description";
        public static final String DURATION = "duration";
        public static final String LOCATION = "location";
        public static final String LORE = "lore";
        public static final String MESSAGE = "message";
        public static final String PLAYER = "player";
        public static final String PLAYER_OR_CONSOLE = "player|-";
        public static final String REASON = "reason";
        public static final String SUBJECT = "subject";
        public static final String USER = "user";
        public static final String USER_UUID = "user uuid";
        public static final String WORLD = "world";
        public static final String XYZ = "x y z";
    }

    public static abstract class LazyLoadedCommandElement {

        @Nullable private CommandElement load;

        public final CommandElement get(final INucleusServiceCollection serviceCollection) {
            if (this.load == null) {
                this.load = this.create(serviceCollection);
            }

            return this.load;
        }

        protected abstract CommandElement create(INucleusServiceCollection serviceCollection);

    }

    private NucleusParameters() {} // entirely static

    public static final CommandElement ONE_TRUE_FALSE = GenericArguments.onlyOne(GenericArguments.bool(Text.of(Keys.BOOL)));

    public static final CommandElement OPTIONAL_ONE_TRUE_FALSE = GenericArguments.optional(ONE_TRUE_FALSE);

    public static final LazyLoadedCommandElement MANY_ENTITY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new SelectorArgument(new DisplayNameArgument(Text.of(Keys.SUBJECT), DisplayNameArgument.Target.PLAYER, serviceCollection),
                    Entity.class, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement MANY_LIVING = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new SelectorArgument(new DisplayNameArgument(Text.of(Keys.SUBJECT), DisplayNameArgument.Target.PLAYER, serviceCollection),
                    Living.class, serviceCollection);
        }
    };

    // users
    private static final LazyLoadedCommandElement MANY_PLAYER_NO_SELECTOR = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new DisplayNameArgument(Text.of(Keys.PLAYER), DisplayNameArgument.Target.PLAYER, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement MANY_USER_NO_SELECTOR = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new DisplayNameArgument(Text.of(Keys.USER), DisplayNameArgument.Target.USER, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement ONE_PLAYER_NO_SELECTOR = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(MANY_PLAYER_NO_SELECTOR.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement MANY_PLAYER = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new SelectorArgument(MANY_PLAYER_NO_SELECTOR.get(serviceCollection), Player.class, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement ONE_PLAYER = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(MANY_PLAYER.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement OPTIONAL_ONE_PLAYER = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.optionalWeak(ONE_PLAYER.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement MANY_PLAYER_OR_CONSOLE = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new SelectorArgument(new DisplayNameArgument(Text.of(Keys.PLAYER_OR_CONSOLE), DisplayNameArgument.Target.PLAYER,
                    serviceCollection), Player.class, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement ONE_PLAYER_OR_CONSOLE = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(MANY_PLAYER_OR_CONSOLE.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement ONE_USER = new LazyLoadedCommandElement() {

        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(
                    new SelectorArgument(new DisplayNameArgument(Text.of(Keys.USER), DisplayNameArgument.Target.USER, serviceCollection),
                            Player.class, serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement ONE_USER_UUID = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(UUIDArgument.user(Text.of(Keys.USER_UUID), serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement ONE_USER_PLAYER_KEY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(
                    new SelectorArgument(new DisplayNameArgument(Text.of(Keys.PLAYER), DisplayNameArgument.Target.USER, serviceCollection),
                            Player.class, serviceCollection));
        }
    };


    public static final LazyLoadedCommandElement ONE_GAME_PROFILE_UUID = new LazyLoadedCommandElement() {
                @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
                    return GenericArguments.onlyOne(UUIDArgument.gameProfile(Text.of(Keys.USER_UUID), serviceCollection));
                }
            };

    public static final LazyLoadedCommandElement ONE_GAME_PROFILE = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(new GameProfileArgument(Text.of(Keys.USER), serviceCollection));
        }
    };

    public static final CommandElement COMMAND = GenericArguments.remainingRawJoinedStrings(Text.of(Keys.COMMAND));

    public static final CommandElement OPTIONAL_COMMAND = GenericArguments.optional(COMMAND);

    public static final CommandElement DESCRIPTION = GenericArguments.remainingRawJoinedStrings(Text.of(Keys.DESCRIPTION));

    public static final CommandElement OPTIONAL_DESCRIPTION = GenericArguments.optional(DESCRIPTION);

    public static final CommandElement LORE = GenericArguments.remainingRawJoinedStrings(Text.of(Keys.LORE));

    public static final CommandElement MESSAGE = GenericArguments.remainingRawJoinedStrings(Text.of(Keys.MESSAGE));

    public static final CommandElement OPTIONAL_MESSAGE = GenericArguments.optional(MESSAGE);

    public static final CommandElement REASON = GenericArguments.remainingRawJoinedStrings(Text.of(Keys.REASON));

    public static final CommandElement OPTIONAL_REASON = GenericArguments.optional(REASON);

    public static final LazyLoadedCommandElement WORLD_PROPERTIES_ENABLED_ONLY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new NucleusWorldPropertiesArgument(Text.of(WORLD), NucleusWorldPropertiesArgument.Type.ENABLED_ONLY, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement OPTIONAL_WEAK_WORLD_PROPERTIES_ENABLED_ONLY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.optionalWeak(WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.optional(WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement WORLD_PROPERTIES_DISABLED_ONLY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return new NucleusWorldPropertiesArgument(Text.of(WORLD), NucleusWorldPropertiesArgument.Type.DISABLED_ONLY, serviceCollection);
        }
    };

    public static final LazyLoadedCommandElement WORLD_PROPERTIES_ALL = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(new NucleusWorldPropertiesArgument(Text.of(WORLD), NucleusWorldPropertiesArgument.Type.ALL, serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement OPTIONAL_WORLD_PROPERTIES_ALL = new LazyLoadedCommandElement() {
                @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
                    return GenericArguments.optionalWeak(WORLD_PROPERTIES_ALL.get(serviceCollection));
                }
            };

    public static final LazyLoadedCommandElement WORLD_PROPERTIES_UNLOADED_ONLY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(
                    new NucleusWorldPropertiesArgument(Text.of(WORLD), NucleusWorldPropertiesArgument.Type.UNLOADED_ONLY, serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement WORLD_PROPERTIES_LOADED_ONLY = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(
                    new NucleusWorldPropertiesArgument(Text.of(WORLD), NucleusWorldPropertiesArgument.Type.LOADED_ONLY, serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement DURATION = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.onlyOne(new TimespanArgument(Text.of(Keys.DURATION), serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement OPTIONAL_DURATION = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.optional(DURATION.get(serviceCollection));
        }
    };

    public static final LazyLoadedCommandElement OPTIONAL_WEAK_DURATION = new LazyLoadedCommandElement() {
        @Override protected CommandElement create(final INucleusServiceCollection serviceCollection) {
            return GenericArguments.optionalWeak(DURATION.get(serviceCollection));
        }
    };

    public static final CommandElement POSITION = GenericArguments.onlyOne(GenericArguments.vector3d(Text.of(XYZ)));

    public static final CommandElement LOCATION = GenericArguments.onlyOne(GenericArguments.location(Text.of(Keys.LOCATION)));

    public static final CommandElement OPTIONAL_LOCATION = GenericArguments.optional(LOCATION);
}
