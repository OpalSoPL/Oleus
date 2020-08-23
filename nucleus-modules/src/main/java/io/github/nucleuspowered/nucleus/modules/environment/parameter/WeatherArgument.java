/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class WeatherArgument extends CommandElement {

    private static final Map<String, Weather> weather = Maps.newHashMap();

    static {
        weather.put("clear", Weathers.CLEAR);
        weather.put("c", Weathers.CLEAR);
        weather.put("sun", Weathers.CLEAR);
        weather.put("rain", Weathers.RAIN);
        weather.put("r", Weathers.RAIN);
        weather.put("storm", Weathers.THUNDER_STORM);
        weather.put("thunder", Weathers.THUNDER_STORM);
        weather.put("t", Weathers.THUNDER_STORM);
    }

    private final IMessageProviderService messageProvider;

    public WeatherArgument(@Nullable final TextComponent key, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String arg = args.next().toLowerCase();
        if (weather.containsKey(arg)) {
            return weather.get(arg);
        }

        throw args.createError(this.messageProvider.getMessageFor(source, "args.weather.noexist", "clear, rain, storm"));
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        try {
            final String a = args.peek();
            return weather.entrySet().stream().filter(x -> x.getKey().toLowerCase().startsWith(a)).map(Map.Entry::getKey).collect(Collectors.toList());
        } catch (final ArgumentParseException e) {
            e.printStackTrace();
            return Lists.newArrayList(weather.keySet());
        }
    }
}
