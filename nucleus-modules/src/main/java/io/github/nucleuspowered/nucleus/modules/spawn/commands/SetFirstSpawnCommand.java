/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import io.github.nucleuspowered.nucleus.configurate.datatypes.LocationNode;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnKeys;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@Command(
        aliases = { "setfirstspawn" },
        basePermission = SpawnPermissions.BASE_SETFIRSTSPAWN,
        commandDescriptionKey = "setfirstspawn")
public class SetFirstSpawnCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer player = context.requirePlayer();
        context.getServiceCollection().storageManager().getGeneralService().getOrNewOnThread()
                .set(SpawnKeys.FIRST_SPAWN_LOCATION,
                        new LocationNode(player.getServerLocation(), player.getRotation()));
        context.sendMessage("command.setfirstspawn.success");
        context.sendMessage("command.setfirstspawn.success2");

        return context.successResult();
    }
}
