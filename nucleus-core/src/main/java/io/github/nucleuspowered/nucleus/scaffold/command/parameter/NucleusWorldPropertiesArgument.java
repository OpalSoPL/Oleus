/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

/**
 * Allows Nucleus commands to get {@link WorldProperties} of disabled worlds.
 */
public class NucleusWorldPropertiesArgument extends CommandElement {

    private final Type type;
    private final IMessageProviderService messageProviderService;

    public NucleusWorldPropertiesArgument(@Nullable final Text key, final Type type, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.type = type;
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String next = args.next();
        final Optional<WorldProperties> owp = this.getChoices().filter(x -> x.getWorldName().equalsIgnoreCase(next)).findFirst();
        if (owp.isPresent()) {
            return owp.get();
        }

        throw args.createError(this.messageProviderService.getMessageFor(source, this.type.key, next));
    }

    @Override public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return this.getChoices().filter(x -> {
            try {
                return x.getWorldName().toLowerCase().startsWith(args.peek());
            } catch (final ArgumentParseException e) {
                return true;
            }
        }).map(WorldProperties::getWorldName).collect(Collectors.toList());
    }

    private Stream<WorldProperties> getChoices() {
        return Sponge.getServer().getAllWorldProperties().stream().filter(this.type.predicate);
    }

    public enum Type {
        DISABLED_ONLY(x -> !x.isEnabled(), "args.worldproperties.noexistdisabled"),
        ENABLED_ONLY(WorldProperties::isEnabled, "args.worldproperties.noexist"),
        LOADED_ONLY(x -> Sponge.getServer().getWorld(x.getUniqueId()).isPresent(), "args.worldproperties.notloaded"),
        UNLOADED_ONLY(x -> !Sponge.getServer().getWorld(x.getUniqueId()).isPresent(), "args.worldproperties.loaded"),
        ALL(x -> true, "args.worldproperties.noexist");

        private final Predicate<WorldProperties> predicate;
        private final String key;

        Type(final Predicate<WorldProperties> predicate, final String key) {
            this.predicate = predicate;
            this.key = key;
        }
    }
}
