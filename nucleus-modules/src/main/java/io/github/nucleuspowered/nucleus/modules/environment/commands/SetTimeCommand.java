/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentPermissions;
import io.github.nucleuspowered.nucleus.modules.environment.parameter.WorldTimeParameter;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.util.TypeTokens;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.util.MinecraftDayTime;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

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

    private final Parameter.Value<MinecraftDayTime> timeParameter;

    @Inject
    public SetTimeCommand(final INucleusServiceCollection serviceCollection) {
        this.timeParameter = Parameter.builder(TypeTokens.MINECRAFT_DAY_TIME).key("time")
                .addParser(new WorldTimeParameter(true, serviceCollection.messageProvider()))
                .build();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONLINE_WORLD_OPTIONAL,
                this.timeParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) {
        final Optional<WorldProperties> pr = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.ONLINE_WORLD_OPTIONAL.getKey());
        if (!pr.isPresent()) {
            return context.errorResult("command.world.player");
        }

        final WorldProperties worldProperties = pr.get();
        final MinecraftDayTime dayTime = context.requireOne(this.timeParameter.getKey());
        worldProperties.setDayTime(dayTime);
        context.sendMessage("command.settime.done2", worldProperties.getKey().asString(),
                Util.getTimeFromDayTime(context.getServiceCollection().messageProvider(), dayTime));
        return context.successResult();
    }
}
