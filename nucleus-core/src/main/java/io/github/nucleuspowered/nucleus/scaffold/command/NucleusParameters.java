/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import io.github.nucleuspowered.nucleus.scaffold.command.parameter.AudienceValueParameter;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.WorldPropertiesValueParameter;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.parameter.CommonParameters;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.CatalogedValueParameters;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A selection of common parameters for making things consistent
 */
public final class NucleusParameters {

    public final static class Keys {

        private Keys() {
        }


        public static final String BOOL = "true|false";
        public static final String COMMAND = "command";
        public static final String CONSOLE = "-";
        public static final String DESCRIPTION = "description";
        public static final String DURATION = "duration";
        public static final String GAME_PROFILE = "game profile";
        public static final String LOCATION = "location";
        public static final String LORE = "lore";
        public static final String MESSAGE = "message";
        public static final String NAME = "name";
        public static final String PLAYER = "player";
        public static final String PLAYER_OR_CONSOLE = "player|-";
        public static final String REASON = "reason";
        public static final String SUBJECT = "subject";
        public static final String USER = "user";
        public static final String USER_UUID = "user uuid";
        public static final String WORLD = "world";
        public static final String XYZ = "x y z";
    }

    public static abstract class LazyLoadedCommandElement<T> {

        private Parameter.@Nullable Value<T> load;

        public final Parameter.Value<T> get(final INucleusServiceCollection serviceCollection) {
            if (this.load == null) {
                this.load = this.create(serviceCollection);
            }

            return this.load;
        }

        protected abstract Parameter.Value<T> create(INucleusServiceCollection serviceCollection);

    }

    private NucleusParameters() {
    } // entirely static

    public static final Parameter.Value<Boolean> ONE_TRUE_FALSE = Parameter.bool().setKey(Keys.BOOL).build();

    public static final Parameter.Value<Boolean> OPTIONAL_ONE_TRUE_FALSE = Parameter.bool().setKey(Keys.BOOL).optional().build();

    public static final Parameter.Value<List<Entity>> MANY_ENTITY = Parameter.builder(new TypeToken<List<Entity>>() {})
            .setKey(Keys.SUBJECT)
            .parser(CatalogedValueParameters.MANY_ENTITIES)
            .build();

    public static final Parameter.Value<List<ServerPlayer>> MANY_PLAYER = Parameter.builder(new TypeToken<List<ServerPlayer>>() {})
            .setKey(Keys.PLAYER)
            .parser(CatalogedValueParameters.MANY_PLAYERS)
            .build();

    public static final Parameter.Value<ServerPlayer> ONE_PLAYER = CommonParameters.PLAYER;

    public static final Parameter.Value<ServerPlayer> OPTIONAL_ONE_PLAYER = CommonParameters.PLAYER_OPTIONAL;

    public static final Parameter.Value<SystemSubject> CONSOLE_FROM_DASH = Parameter
            .builder(SystemSubject.class)
            .setKey(Keys.CONSOLE)
            .parser(VariableValueParameters.literalBuilder(SystemSubject.class)
                    .setLiteral(Collections.singletonList("-"))
                    .setReturnValue(Sponge::getSystemSubject)
                    .build())
            .build();

    public static final Parameter.Value<User> ONE_USER = Parameter.user().setKey(Keys.USER).build();

    public static final Parameter.Value<GameProfile> GAME_PROFILE =
            Parameter.builder(TypeToken.get(GameProfile.class)).setKey(Keys.GAME_PROFILE).parser(CatalogedValueParameters.GAME_PROFILE).build();

    public static final Parameter.Value<Collection<GameProfile>> MANY_GAME_PROFILES =
            Parameter.builder(new TypeToken<Collection<GameProfile>>() {}).setKey(Keys.GAME_PROFILE).parser(CatalogedValueParameters.MANY_GAME_PROFILES).build();

    public static final Parameter.Value<String> COMMAND = Parameter.remainingJoinedStrings().setKey(Keys.COMMAND).build();

    public static final Parameter.Value<String> OPTIONAL_COMMAND = Parameter.remainingJoinedStrings().setKey(Keys.COMMAND).optional().build();

    public static final Parameter.Value<String> DESCRIPTION = Parameter.remainingJoinedStrings().setKey(Keys.DESCRIPTION).build();

    public static final Parameter.Value<String> OPTIONAL_DESCRIPTION = Parameter.remainingJoinedStrings().setKey(Keys.DESCRIPTION).optional().build();

    public static final Parameter.Value<Component> LORE = Parameter.formattingCodeTextOfRemainingElements().setKey(Keys.LORE).build();

    public static final Parameter.Value<String> MESSAGE = Parameter.remainingJoinedStrings().setKey(Keys.MESSAGE).build();

    public static final Parameter.Value<String> OPTIONAL_MESSAGE = Parameter.remainingJoinedStrings().setKey(Keys.MESSAGE).optional().build();

    public static final Parameter.Value<String> REASON = Parameter.remainingJoinedStrings().setKey(Keys.REASON).build();

    public static final Parameter.Value<String> OPTIONAL_REASON = Parameter.remainingJoinedStrings().setKey(Keys.REASON).optional().build();

    public static final Parameter.Value<WorldProperties> WORLD_PROPERTIES_ENABLED_ONLY =
            Parameter.builder(WorldProperties.class)
                    .setKey(Keys.WORLD)
                    .parser(new WorldPropertiesValueParameter(
                            WorldProperties::isEnabled,
                            key -> Component.text(key.getFormatted() + " is not a disabled world properties.")))
                    .build();

    public static final Parameter.Value<WorldProperties> OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY =
            CommonParameters.ONLINE_WORLD_PROPERTIES_ONLY_OPTIONAL;

    public static final Parameter.Value<WorldProperties> WORLD_PROPERTIES_DISABLED_ONLY =
            Parameter.builder(WorldProperties.class)
                    .setKey(Keys.WORLD)
                    .parser(new WorldPropertiesValueParameter(
                            x -> !x.isEnabled(),
                            key -> Component.text(key.getFormatted() + " is not a disabled world properties.")))
                    .build();

    public static final Parameter.Value<WorldProperties> WORLD_PROPERTIES_ALL = CommonParameters.ALL_WORLD_PROPERTIES;

    public static final Parameter.Value<WorldProperties> OPTIONAL_WORLD_PROPERTIES_ALL =
            Parameter.worldProperties(false).optional().setKey(Keys.WORLD).build();

    public static final Parameter.Value<WorldProperties> WORLD_PROPERTIES_UNLOADED_ONLY =
            Parameter.builder(WorldProperties.class)
                    .setKey(Keys.WORLD)
                    .parser(new WorldPropertiesValueParameter(
                            x -> !x.getWorld().isPresent(),
                            key -> Component.text(key.getFormatted() + " is not an unloaded world properties.")))
                    .build();

    public static final Parameter.Value<WorldProperties> WORLD_PROPERTIES_LOADED_ONLY = CommonParameters.ONLINE_WORLD_PROPERTIES_ONLY;

    public static final Parameter.Value<Duration> DURATION = Parameter.duration().setKey(Keys.DURATION).build();

    public static final Parameter.Value<Duration> OPTIONAL_DURATION = Parameter.duration().setKey(Keys.DURATION).optional().build();

    public static final Parameter.Value<Vector3d> POSITION = Parameter.vector3d().setKey(Keys.XYZ).build();

    public static final Parameter.Value<ServerLocation> LOCATION = Parameter.location().setKey(Keys.LOCATION).build();

    public static final Parameter.Value<ServerLocation> OPTIONAL_LOCATION = Parameter.location().setKey(Keys.LOCATION).optional().build();

    public static final Parameter.Value<List<Audience>> MULTI_AUDIENCE = Parameter.builder(new TypeToken<List<Audience>>() {})
            .setKey("audience")
            .parser(new AudienceValueParameter())
            .build();


    public static final Parameter.Value<String> STRING_NAME = Parameter.string().setKey(Keys.NAME).build();

}
