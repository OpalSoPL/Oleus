/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.parameter;

import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.command.parameter.managed.standard.ResourceKeyedValueParameters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class AudienceValueParameter implements ValueParameter<List<Audience>> {

    @Override
    public List<String> complete(final CommandContext context, final String currentInput) {
        if (currentInput.equals("-")) {
            return Collections.singletonList("-");
        }
        return ResourceKeyedValueParameters.MANY_PLAYERS.get().complete(context, currentInput);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Optional<? extends List<Audience>> getValue(
            final Parameter.Key<? super List<Audience>> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String r = reader.peekString();
        if (r.equals("-")) {
            reader.parseString();
            return Optional.of(Collections.singletonList(Sponge.getSystemSubject()));
        }
        return ResourceKeyedValueParameters.MANY_PLAYERS.get().getValue((Parameter.Key) parameterKey, reader, context);
    }
}
