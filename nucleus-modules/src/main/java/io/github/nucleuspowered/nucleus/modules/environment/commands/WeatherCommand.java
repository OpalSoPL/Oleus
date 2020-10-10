/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentKeys;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentPermissions;
import io.github.nucleuspowered.nucleus.modules.environment.config.EnvironmentConfig;
import io.github.nucleuspowered.nucleus.modules.environment.parameter.WeatherArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;

import java.time.Duration;
import java.util.Optional;

@EssentialsEquivalent({"thunder", "sun", "weather", "sky", "storm", "rain"})
@Command(
        aliases = {"weather"},
        basePermission = EnvironmentPermissions.BASE_WEATHER,
        commandDescriptionKey = "weather",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = EnvironmentPermissions.EXEMPT_COOLDOWN_WEATHER),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission =  EnvironmentPermissions.EXEMPT_WARMUP_WEATHER),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = EnvironmentPermissions.EXEMPT_COST_WEATHER)
        },
        associatedPermissions = EnvironmentPermissions.WEATHER_EXEMPT_LENGTH
)
public class WeatherCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final Parameter.Value<Weather> weatherParameter;

    @Inject
    public WeatherCommand(final IMessageProviderService messageProviderService) {
        this.weatherParameter = Parameter.builder(Weather.class)
                .parser(new WeatherArgument(messageProviderService))
                .setKey("weather")
                .build();
    }

    private long max = Long.MAX_VALUE;

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.max = serviceCollection.configProvider().getModuleConfig(EnvironmentConfig.class).getMaximumWeatherTimespan();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY,
                this.weatherParameter,
                NucleusParameters.OPTIONAL_DURATION
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // We can predict the weather on multiple worlds now!
        final WorldProperties wp = context.getWorldPropertiesOrFromSelf(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.getKey());
        final ServerWorld w = wp.getWorld()
            .orElseThrow(() -> context.createException("args.worldproperties.notloaded", wp.getKey().asString()));

        // Get whether we locked the weather.
        if (context.getServiceCollection().storageManager().getWorldOnThread(w.getKey())
                .map(x -> x.get(EnvironmentKeys.LOCKED_WEATHER).orElse(false)).orElse(false)) {
            // Tell the user to unlock first.
            return context.errorResult("command.weather.locked", w.getKey().asString());
        }

        // Houston, we have a world! Now, what was the forecast?
        final Weather we = context.requireOne(this.weatherParameter);

        // Have we gotten an accurate forecast? Do we know how long this weather spell will go on for?
        final Optional<Long> oi = context.getOne(NucleusParameters.OPTIONAL_DURATION).map(Duration::getSeconds);

        // Even weather masters have their limits. Sometimes.
        if (this.max > 0 && oi.orElse(Long.MAX_VALUE) > this.max && !context.testPermission(EnvironmentPermissions.WEATHER_EXEMPT_LENGTH)) {
            return context.errorResult("command.weather.toolong", context.getTimeString(this.max));
        }

        if (oi.isPresent()) {
            // YES! I should get a job at the weather service and show them how it's done!
            Task.builder().execute(() -> w.setWeather(we, oi.get() * 20L)).submit(context.getServiceCollection().pluginContainer());
            context.sendMessage("command.weather.time", we.getName(), w.getName(), context.getTimeString(oi.get()));
        } else {
            // No, probably because I've already gotten a job at the weather service...
            Task.builder().execute(() -> w.setWeather(we)).submit(context.getServiceCollection().pluginContainer());
            context.sendMessage("command.weather.set", we.getName(), w.getName());
        }

        // The weather control device has been activated!
        return context.successResult();
    }


}
