/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus;

import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;

@Command(
        aliases = "reload",
        basePermission = CorePermissions.BASE_NUCLEUS_RELOAD,
        commandDescriptionKey = "nucleus.reload",
        parentCommand = NucleusCommand.class
)
public class ReloadCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        try {
            context.getServiceCollection().reloadableService().fireReloadables(context.getServiceCollection());
            context.sendMessage("command.reload.one");
            context.sendMessage("command.reload.two");
            return context.successResult();
        } catch (final Throwable e) {
            return context.errorResult("command.reload.errorone");
        }
    }
}
