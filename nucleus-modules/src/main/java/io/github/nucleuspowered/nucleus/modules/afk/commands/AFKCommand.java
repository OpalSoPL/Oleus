/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.commands;

import io.github.nucleuspowered.nucleus.modules.afk.AFKPermissions;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@Command(aliases = {"afk", "away"}, basePermission = AFKPermissions.BASE_AFK, commandDescriptionKey = "afk")
@EssentialsEquivalent({"afk", "away"})
public class AFKCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        if (!context.testPermission(AFKPermissions.AFK_EXEMPT_TOGGLE)) {
            return context.errorResult("command.afk.exempt");
        }

        final ServerPlayer src = context.getIfPlayer();
        final AFKHandler afkHandler = context.getServiceCollection().getServiceUnchecked(AFKHandler.class);
        final boolean isAFK = afkHandler.isAFK(src.getUniqueId());

        if (isAFK) {
            afkHandler.stageUserActivityUpdate(src);
        } else if (!afkHandler.setAfkInternal(src.getUniqueId(), true)) {
            return context.errorResult("command.afk.notset");
        }

        return context.successResult();
    }

}
