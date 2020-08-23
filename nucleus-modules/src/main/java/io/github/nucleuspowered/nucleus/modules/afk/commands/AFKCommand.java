/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.commands;

import io.github.nucleuspowered.nucleus.modules.afk.AFKPermissions;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.entity.living.player.Player;
@Command(aliases = {"afk", "away"}, basePermission = AFKPermissions.BASE_AFK, commandDescriptionKey = "afk")
@EssentialsEquivalent({"afk", "away"})
public class AFKCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final IPermissionService permissionService = context.getServiceCollection().permissionService();
        if (!permissionService.isOpOnly() && context.testPermission(AFKPermissions.AFK_EXEMPT_TOGGLE)) {
            return context.errorResult("command.afk.exempt");
        }

        final Player src = context.getIfPlayer();
        final AFKHandler afkHandler = context.getServiceCollection().getServiceUnchecked(AFKHandler.class);
        final boolean isAFK = afkHandler.isAFK(src);

        if (isAFK) {
            afkHandler.stageUserActivityUpdate(src);
        } else if (!afkHandler.setAfkInternal(src, context.getCause(), true)) {
            return context.errorResult("command.afk.notset");
        }

        return context.successResult();
    }

}
