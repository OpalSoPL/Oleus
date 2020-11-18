/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back.commands;

import io.github.nucleuspowered.nucleus.modules.back.BackPermissions;
import io.github.nucleuspowered.nucleus.modules.back.services.BackHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;

@Command(
        aliases = "clearback",
        basePermission = BackPermissions.BASE_CLEARBACK,
        commandDescriptionKey = "clearback"
)
public class ClearBackCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_USER
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User target = context.getUserFromArgs();
        final boolean isSelf = context.is(target);
        if (!isSelf) {
            if (!context.testPermission(BackPermissions.OTHERS_CLEARBACK)) {
                // no permission
                return context.errorResult("command.clearback.other.noperm");
            }
        }

        context.getServiceCollection().getServiceUnchecked(BackHandler.class).removeLastLocation(target.getUniqueId());
        context.sendMessage("command.clearback.success", target.getName());
        return context.successResult();
    }
}
