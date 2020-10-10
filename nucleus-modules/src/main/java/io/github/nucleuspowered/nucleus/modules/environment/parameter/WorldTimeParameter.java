/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.parameter;

import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.util.TemporalUnits;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses an argument for world time. Could be either:
 *
 * <ul>
 *     <li>[0-23]h, for 24 hour time.</li>
 *     <li>[1-12][am|pm], for 12 hour time.</li>
 *     <li>[0-23999], for ticks.</li>
 * </ul>
 *
 * It could also be one of the pre-defined keywords:
 *
 * <ul>
 *     <li>dawn, sunrise: 0 ticks (6 am)</li>
 *     <li>day, daytime, morning: 1000 ticks (7 am)</li>
 *     <li>noon, afternoon: 6000 ticks (12 pm)</li>
 *     <li>dusk, evening, sunset: 12000 ticks (6 pm)</li>
 *     <li>night: 14000 ticks (8 pm)</li>
 *     <li>midnight: 18000 ticks (12 am)</li>
 * </ul>
 */
public class WorldTimeParameter implements ValueParameter<Duration> {

    private static final int TICKS_IN_DAY = 24000;
    private static final HashMap<String, Duration> TICK_ALIASES = Maps.newHashMap();
    private static final Pattern tfh = Pattern.compile("^(\\d{1,2})[hH]$");
    private static final Pattern ampm = Pattern.compile("^(\\d{1,2})(a[m]?|p[m]?)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern ticks = Pattern.compile("^(\\d{1,5})$");

    private static final Duration DAWN = Duration.of(0, TemporalUnits.MINECRAFT_TICKS);
    private static final Duration DAY = Duration.of(1000, TemporalUnits.MINECRAFT_TICKS);
    private static final Duration NOON = Duration.of(6000, TemporalUnits.MINECRAFT_TICKS);
    private static final Duration DUSK = Duration.of(12000, TemporalUnits.MINECRAFT_TICKS);
    private static final Duration NIGHT = Duration.of(14000, TemporalUnits.MINECRAFT_TICKS);
    private static final Duration MIDNIGHT = Duration.of(18000, TemporalUnits.MINECRAFT_TICKS);

    private final boolean allowAliases;

    // Thanks to http://minecraft.gamepedia.com/Day-night_cycle
    static {
        TICK_ALIASES.put("dawn", DAWN);
        TICK_ALIASES.put("sunrise", DAWN);
        TICK_ALIASES.put("morning", DAY);
        TICK_ALIASES.put("day", DAY);
        TICK_ALIASES.put("daytime", DAY);
        TICK_ALIASES.put("noon", NOON);
        TICK_ALIASES.put("afternoon", NOON);
        TICK_ALIASES.put("dusk", DUSK);
        TICK_ALIASES.put("sunset", DUSK);
        TICK_ALIASES.put("evening", DUSK);
        TICK_ALIASES.put("night", NIGHT);
        TICK_ALIASES.put("midnight", MIDNIGHT);
    }

    private final IMessageProviderService messageProvider;

    public WorldTimeParameter(final boolean allowAliases, final IMessageProviderService messageProviderService) {
        this.allowAliases = allowAliases;
        this.messageProvider = messageProviderService;
    }

    private Duration getValue(final CommandContext source, final ArgumentReader.Mutable reader, final String arg) throws ArgumentParseException {
        if (this.allowAliases && TICK_ALIASES.containsKey(arg)) {
            return TICK_ALIASES.get(arg);
        }
        final Audience audience = source.getCause().getAudience();

        // <number>h
        final Matcher m1 = tfh.matcher(arg);
        if (m1.matches()) {
            // Get the number, multiply by 1000, return.
            long i = Long.parseLong(m1.group(1));
            if (i > 23 || i < 0) {
                throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.24herror"));
            }

            i -= 6;
            if (i < 0) {
                i += 24;
            }

            return Duration.of(i, TemporalUnits.MINECRAFT_DAYS);
        }

        // <number>am,pm
        final Matcher m2 = ampm.matcher(arg);
        if (m2.matches()) {
            // Get the number, multiply by 1000, return.
            int i = Integer.parseInt(m2.group(1));
            if (i > 12 || i < 1) {
                throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.12herror"));
            }

            // Modify to 24 hour time, based on am/pm
            final String id = m2.group(2).toLowerCase();
            if (id.startsWith("p") && i < 12) {
                // 11 pm -> 23, 12 pm -> 12.
                i += 12;
            } else if (id.startsWith("a") && i == 12) {
                // 12 am -> 0
                i = 0;
            }

            // Adjust for Minecraft time.
            i -= 6;
            if (i < 0) {
                i += 24;
            }

            return Duration.of(i, TemporalUnits.MINECRAFT_DAYS);
            return x -> res;
        }

        // 0 -> 23999
        if (ticks.matcher(arg).matches()) {
            final long i = Long.parseLong(arg);
            if (i >= 0 && i <= 23999) {
                return x -> i;
            }

            throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.ticks"));
        }

        throw reader.createException(this.messageProvider.getMessageFor(audience, "args.worldtime.error", arg));
    }

    @Override
    public List<String> complete(final CommandContext context, final String currentInput) {
        return WorldTimeParameter.TICK_ALIASES.keySet().stream().filter(x -> x.startsWith(currentInput)).collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Function<Duration, Duration>> getValue(
            final Parameter.Key<? super Function<Duration, Duration>> parameterKey,
            final ArgumentReader.Mutable reader,
            final CommandContext.Builder context) throws ArgumentParseException {
        final String arg = reader.parseString();
        return Optional.of(this.getValue(context, reader, arg));
    }

    private static final class RoundUp implements Function<Duration, Duration> {

        private final Duration target;

        private RoundUp(final Duration target) {
            this.target = target;
        }

        @Override
        public final Duration apply(final Duration value) {
            final value.get(TemporalUnits.DAYS)
            // 23999 is the max tick number
            // Get the time of day
            final long remainder = value % TICKS_IN_DAY;

            if (this.target == remainder) {
                // no advancement
                return value;
            }

            if (this.target < remainder) {
                // target is below remainder, we need to get to target on next day
                value += TICKS_IN_DAY;
            }

            // remove remainder, add target
            return value - remainder + this.target;
        }
    }
}
