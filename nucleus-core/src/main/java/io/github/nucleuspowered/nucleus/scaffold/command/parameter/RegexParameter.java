package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class RegexParameter implements ValueParameter<String> {

    private final Pattern pattern;
    private final String key;
    private final IMessageProviderService messageProviderService;

    public RegexParameter(final Pattern pattern, final String key, final IMessageProviderService messageProviderService) {
        this.pattern = pattern;
        this.key = key;
        this.messageProviderService = messageProviderService;
    }

    @Override
    public List<String> complete(final CommandContext context, final String currentInput) {
        return Collections.emptyList();
    }

    @Override
    public Optional<? extends String> getValue(final Parameter.Key<? super String> parameterKey, final ArgumentReader.Mutable reader, final CommandContext.Builder context)
            throws ArgumentParseException {
        final String toParse = reader.parseString();
        if (this.pattern.matcher(toParse).matches()) {
            return Optional.of(toParse);
        }
        throw reader.createException(this.messageProviderService.getMessageFor(context.getCause().getAudience(), this.key));
    }

}
