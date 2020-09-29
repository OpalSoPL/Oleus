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
        aliases = "debug",
        basePermission = CorePermissions.BASE_NUCLEUS_DEBUG,
        commandDescriptionKey = "nucleus.debug",
        parentCommand = NucleusCommand.class,
        hasExecutor = false
)
public class DebugCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) {
        return context.failResult();
    }

}
