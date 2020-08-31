/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import net.kyori.adventure.text.TextComponent;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.command.parameter.managed.clientcompletion.ClientCompletionType;
import org.spongepowered.api.command.parameter.managed.clientcompletion.ClientCompletionTypes;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldPropertiesValueParameter implements ValueParameter<WorldProperties> {

    private final Predicate<WorldProperties> filter;
    private final Function<ResourceKey, TextComponent> errorMessageGenerator;

    public WorldPropertiesValueParameter(final Predicate<WorldProperties> filter, final Function<ResourceKey, TextComponent> errorMessageGenerator) {
        this.filter = filter;
        this.errorMessageGenerator = errorMessageGenerator;
    }

    @Override
    public List<String> complete(final CommandContext context) {
        return Sponge.getServer().getWorldManager().getAllProperties().stream()
                .filter(this.filter)
                .map(y -> y.getKey().toString())
                .collect(Collectors.toList());
    }

    @Override public Optional<? extends WorldProperties> getValue(final Parameter.Key<? super WorldProperties> parameterKey,
            final ArgumentReader.Mutable reader, final CommandContext.Builder context) throws ArgumentParseException {
        final ResourceKey key = reader.parseResourceKey();
        final Optional<WorldProperties> ow =
                Sponge.getServer().getWorldManager().getProperties(key)
                        .filter(this.filter);
        if (ow.isPresent()) {
            return ow;
        }
        throw reader.createException(this.errorMessageGenerator.apply(key));
    }

    @Override public List<ClientCompletionType> getClientCompletionType() {
        return Collections.singletonList(ClientCompletionTypes.RESOURCE_KEY.get());
    }

}
