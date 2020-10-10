/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentPermissions;
import io.github.nucleuspowered.nucleus.modules.environment.parameter.WorldTimeParameter;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.storage.WorldProperties;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongFunction;

@EssentialsEquivalent(value = {"time", "day", "night"}, isExact = false, notes = "A time MUST be specified.")
@Command(
        aliases = {"set", "#settime", "#timeset"},
        basePermission = EnvironmentPermissions.BASE_TIME_SET,
        commandDescriptionKey = "time.set",
        parentCommand = TimeCommand.class,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = EnvironmentPermissions.EXEMPT_COOLDOWN_TIME_SET),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission =  EnvironmentPermissions.EXEMPT_WARMUP_TIME_SET),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = EnvironmentPermissions.EXEMPT_COST_TIME_SET)
        }
)
public class SetTimeCommand implements ICommandExecutor {

    private final Parameter.Value<Function<Duration, Duration>> timeParameter;

    @Inject
    public SetTimeCommand(final INucleusServiceCollection serviceCollection) {
        this.timeParameter = Parameter.builder(new TypeToken<LongFunction<Long>>() {}).setKey("time")
                .parser(new WorldTimeParameter(allowAliases, serviceCollection))
                .build()
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY,
                this.timeParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) {
        final Optional<WorldProperties> pr = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.getKey());
        if (!pr.isPresent()) {
            return context.errorResult("command.world.player");
        }

        MinecraftTemporalUnit

        final LongFunction<Long> tick = context.requireOne(this.timeParameter.getKey());
        final long time = tick.apply(pr.get().getWorld().get().getTm.getWorldTime());
        pr.setWorldTime(time);
        context.sendMessage("command.settime.done2", pr.getWorldName(),
                Util.getTimeFromTicks(context.getServiceCollection().messageProvider(), time));
        return context.successResult();
    }
}
