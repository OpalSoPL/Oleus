/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.modules.teleport.TeleportKeys;
import io.github.nucleuspowered.nucleus.modules.teleport.TeleportPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

@EssentialsEquivalent("tptoggle")
@Command(
        aliases = "tptoggle",
        basePermission = TeleportPermissions.BASE_TPTOGGLE,
        commandDescriptionKey = "tptoggle",
        associatedPermissions = TeleportPermissions.TPTOGGLE_EXEMPT
)
public class TeleportToggleCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection service) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final IUserPreferenceService ups = context.getServiceCollection().userPreferenceService();
        final Player pl = context.getIfPlayer();
        final boolean toggle = ups.get(pl.uniqueId(), TeleportKeys.TELEPORT_TOGGLE).orElse(true);
        final boolean flip = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE).orElseGet(() -> !toggle);
        ups.set(pl.uniqueId(), TeleportKeys.TELEPORT_TOGGLE, flip);
        context.sendMessage(
                "command.tptoggle.success", flip ? "loc:standard.enabled" : "loc:standard.disabled"
        );
        return context.successResult();
    }
}
