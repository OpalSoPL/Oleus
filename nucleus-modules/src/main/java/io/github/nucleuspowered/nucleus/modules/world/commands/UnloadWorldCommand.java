/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(
        aliases = {"unload"},
        basePermission = WorldPermissions.BASE_WORLD_UNLOAD,
        commandDescriptionKey = "world.unload",
        parentCommand = WorldCommand.class
)
public class UnloadWorldCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of(NucleusParameters.ONLINE_WORLD, "t", "transfer")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            NucleusParameters.ONLINE_WORLD
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerWorld world = context.requireOne(NucleusParameters.ONLINE_WORLD);
        final Optional<ServerWorld> transferWorld = context.getOne(NucleusParameters.ONLINE_WORLD);
        final List<Player> playerCollection = Sponge.server().onlinePlayers().stream().filter(x -> x.getWorld().equals(world)).collect(Collectors.toList());

        transferWorld.ifPresent(serverWorld -> playerCollection.forEach(x -> x.transferToWorld(serverWorld)));

        return UnloadWorldCommand.unloadWorld(context, world);
    }

    private static ICommandResult unloadWorld(final ICommandContext context, final ServerWorld world) {
        context.sendMessage("command.world.unload.start", world.getKey().asString());
        Sponge.server().worldManager().unloadWorld(world).handle((result, exception) -> {
            context.getServiceCollection().schedulerService().runOnMainThread(() -> {
                if (exception == null && result) {
                    context.sendMessage("command.world.unload.success", world.getKey().asString());
                } else {
                    context.sendMessage("command.world.unload.failed", world.getKey().asString());
                }
            });
            return null;
        });
        return context.successResult();
    }
}
