/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fun.commands;

import io.github.nucleuspowered.nucleus.modules.fun.FunPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.Ticks;

@EssentialsEquivalent("burn")
@Command(
        aliases = {"ignite", "burn"},
        basePermission = FunPermissions.BASE_IGNITE,
        commandDescriptionKey = "ignite",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = FunPermissions.EXEMPT_COOLDOWN_IGNITE),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = FunPermissions.EXEMPT_WARMUP_IGNITE),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = FunPermissions.EXEMPT_COST_IGNITE)
        },
        associatedPermissions = FunPermissions.OTHERS_IGNITE
)
public class IgniteCommand implements ICommandExecutor {

    private final Parameter.Value<Integer> ticks = Parameter.builder(Integer.class)
            .key("ticks")
            .addParser(VariableValueParameters.integerRange().min(0).build())
            .build();

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherUserPermissionElement(FunPermissions.OTHERS_IGNITE),
                this.ticks
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer target = context.getPlayerFromArgs();
        final int ticksInput = context.requireOne(this.ticks);
        final GameMode gm = target.get(Keys.GAME_MODE).orElseGet(GameModes.SURVIVAL);
        if (gm == GameModes.CREATIVE.get() || gm == GameModes.SPECTATOR.get()) {
            return context.errorResult("command.ignite.gamemode", target.name());
        }

        if (target.offer(Keys.FIRE_TICKS, Ticks.of(ticksInput)).isSuccessful()) {
            context.sendMessage("command.ignite.success", target.name(), String.valueOf(ticksInput));
            return context.successResult();
        } else {
            return context.errorResult("command.ignite.error", target.name());
        }
    }
}
