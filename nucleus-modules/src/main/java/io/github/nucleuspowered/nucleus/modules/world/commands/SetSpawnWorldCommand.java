/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

@Command(
        aliases = {"setspawn"},
        basePermission = WorldPermissions.BASE_WORLD_SETSPAWN,
        commandDescriptionKey = "world.setspawn",
        parentCommand = WorldCommand.class
)
public class SetSpawnWorldCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.LOCATION
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<ServerLocation> serverLocation = context.getOne(NucleusParameters.LOCATION);
        final ServerLocation location;
        if (serverLocation.isPresent()) {
            location = serverLocation.get();
        } else {
            location = context.requirePlayer().getServerLocation();
        }

        Sponge.getServer().getWorldManager().getProperties(location.getWorldKey())
                .get()
                .setSpawnPosition(location.getBlockPosition());
        context.sendMessage("command.world.setspawn.success");
        return context.successResult();
    }
}
