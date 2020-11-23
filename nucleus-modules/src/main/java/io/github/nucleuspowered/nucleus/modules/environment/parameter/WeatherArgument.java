/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.parameter;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WeatherArgument implements ValueParameter<Weather> {

    private static final Map<String, Weather> WEATHERS = new HashMap<>();

    static {
        WEATHERS.put("clear", Weathers.CLEAR.get());
        WEATHERS.put("c", Weathers.CLEAR.get());
        WEATHERS.put("sun", Weathers.CLEAR.get());
        WEATHERS.put("rain", Weathers.RAIN.get());
        WEATHERS.put("r", Weathers.RAIN.get());
        WEATHERS.put("storm", Weathers.THUNDER.get());
        WEATHERS.put("thunder", Weathers.THUNDER.get());
        WEATHERS.put("t", Weathers.THUNDER.get());
    }

    private final IMessageProviderService messageProvider;

    public WeatherArgument(final IMessageProviderService messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public List<String> complete(final CommandContext context, final String currentInput) {
        return WeatherArgument.WEATHERS.keySet().stream()
                .filter(weather -> weather.toLowerCase().startsWith(currentInput))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Weather> getValue(
            final Parameter.Key<? super Weather> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String arg = reader.parseString().toLowerCase();
        if (WeatherArgument.WEATHERS.containsKey(arg)) {
            return Optional.of(WeatherArgument.WEATHERS.get(arg));
        }

        throw reader.createException(this.messageProvider.getMessageFor(context.getCause().getAudience(), "args.weather.noexist", "clear, rain, storm"));
    }
}
