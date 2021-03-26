/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerLocation;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

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
            location = context.requirePlayer().serverLocation();
        }

        return location.worldIfAvailable()
                .map(world -> {
                    world.properties().setSpawnPosition(location.blockPosition());
                    context.sendMessage("command.world.setspawn.success");
                    return context.successResult();
                })
                .orElseGet(() -> context.errorResult("command.world.setspawn.worldnotloaded", location.worldKey()));
    }
}
