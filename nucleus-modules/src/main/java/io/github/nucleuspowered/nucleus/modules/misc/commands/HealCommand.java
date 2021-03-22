/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.modules.misc.MiscPermissions;
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
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Ticks;

@EssentialsEquivalent("heal")
@Command(
        aliases = {"heal"},
        basePermission = MiscPermissions.BASE_HEAL,
        commandDescriptionKey = "heal",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MiscPermissions.EXEMPT_COOLDOWN_HEAL),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MiscPermissions.EXEMPT_WARMUP_HEAL),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MiscPermissions.EXEMPT_COST_HEAL)
        },
        associatedPermissions = MiscPermissions.OTHERS_HEAL
)
public class HealCommand implements ICommandExecutor { // extends AbstractCommand.SimpleTargetOtherPlayer {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier()
                        .createOnlyOtherPlayerPermissionElement(MiscPermissions.OTHERS_HEAL)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player pl = context.getPlayerFromArgs();
        if (pl.offer(Keys.HEALTH, pl.get(Keys.MAX_HEALTH).orElse(20.0)).isSuccessful()) {
            pl.offer(Keys.FIRE_TICKS, Ticks.zero());
            context.sendMessageTo(pl, "command.heal.success.self");
            if (!context.is(pl)) {
                context.sendMessage("command.heal.success.other", pl.name());
            }

            return context.successResult();
        } else {
            return context.errorResult("command.heal.error");
        }
    }
}
