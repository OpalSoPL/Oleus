/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import io.github.nucleuspowered.nucleus.modules.spawn.SpawnKeys;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
@Command(
        aliases = { "del", "rm" },
        basePermission = SpawnPermissions.BASE_SETFIRSTSPAWN_DEL,
        commandDescriptionKey = "setfirstspawn.del",
        parentCommand = SetFirstSpawnCommand.class
)
public class RemoveFirstSpawnCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        context.getServiceCollection().storageManager()
                .getGeneralService()
                .getOrNewOnThread()
                .remove(SpawnKeys.FIRST_SPAWN_LOCATION);
        context.sendMessage("command.setfirstspawn.remove");
        return context.successResult();
    }

}
