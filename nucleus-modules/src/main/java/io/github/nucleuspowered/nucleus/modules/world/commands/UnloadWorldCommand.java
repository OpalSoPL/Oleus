/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.storage.WorldProperties;

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
                Flag.builder()
                        .setRequirement(commandCause -> serviceCollection.permissionService().hasPermission(commandCause, WorldPermissions.BASE_WORLD_DISABLE))
                        .alias("d")
                        .alias("disable")
                        .build(),
                Flag.of(NucleusParameters.WORLD_PROPERTIES_LOADED_ONLY, "t", "transfer")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            NucleusParameters.WORLD_PROPERTIES_ENABLED_ONLY
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties worldProperties = context.requireOne(NucleusParameters.WORLD_PROPERTIES_ENABLED_ONLY);
        final Optional<WorldProperties> transferWorld = context.getOne(NucleusParameters.WORLD_PROPERTIES_LOADED_ONLY);
        final boolean disable = context.hasFlag("d");

        final Optional<ServerWorld> worldOptional = worldProperties.getWorld();
        if (!worldOptional.isPresent()) {
            // Not loaded.
            if (disable) {
                UnloadWorldCommand.disable(worldProperties, context, false);
            }

            return context.errorResult("command.world.unload.alreadyunloaded", worldProperties.getKey().asString());
        }

        final ServerWorld world = worldOptional.get();
        final List<Player> playerCollection = Sponge.getServer().getOnlinePlayers().stream().filter(x -> x.getWorld().equals(world)).collect(Collectors.toList());

        if ((transferWorld.isPresent() && transferWorld.get().isEnabled())) {
            playerCollection.forEach(x -> x.transferToWorld(transferWorld.get().getWorld().get(), transferWorld.get().getSpawnPosition().toDouble()));
        }

        return UnloadWorldCommand.unloadWorld(context, world, disable);
    }

    private static ICommandResult disable(final WorldProperties worldProperties,
            final ICommandContext context,
            final boolean messageOnError) {
        if (worldProperties.isEnabled()) {
            return DisableWorldCommand.disableWorld(context, worldProperties);
        } else if (messageOnError) {
            return context.errorResult("command.world.disable.alreadydisabled", worldProperties.getKey().asString());
        }

        return context.successResult();
    }

    private static ICommandResult unloadWorld(final ICommandContext context, final ServerWorld world, final boolean disable) {
        final WorldProperties worldProperties = world.getProperties();
        context.sendMessage("command.world.unload.start", worldProperties.getKey().asString());
        Sponge.getServer().getWorldManager().unloadWorld(world).handle((result, exception) -> {
            context.getServiceCollection().schedulerService().runOnMainThread(() -> {
                if (exception == null && result) {
                    context.sendMessage("command.world.unload.success", worldProperties.getKey().asString());
                    if (disable) {
                        UnloadWorldCommand.disable(worldProperties, context, true).getErrorMessage(context)
                            .ifPresent(context::sendMessageText);
                    }
                } else {
                    context.sendMessage("command.world.unload.failed", worldProperties.getKey().asString());
                }
            });
            return null;
        });
        return context.successResult();
    }
}
