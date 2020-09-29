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

@Command(
        aliases = "save",
        basePermission = CorePermissions.BASE_NUCLEUS_SAVE,
        commandDescriptionKey = "nucleus.save",
        parentCommand = NucleusCommand.class,
        async = true
)
public class SaveCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) {
        context.sendMessage("command.nucleus.save.start");
        context.getServiceCollection().storageManager().saveAll().thenApply(x -> {
            context.sendMessage("command.nucleus.save.complete");
            return null;
        });
        return context.successResult();
    }
}
