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
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = {"load"},
        basePermission = WorldPermissions.BASE_WORLD_LOAD,
        commandDescriptionKey = "world.load",
        parentCommand = WorldCommand.class
)
public class LoadWorldCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
            Flag.builder().setRequirement(commandCause -> serviceCollection.permissionService()
                    .hasPermission(commandCause, WorldPermissions.BASE_WORLD_ENABLE))
                .alias("e")
                .alias("enable")
                .build()
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
        if (!worldProperties.isEnabled() && !context.hasFlag("e")) {
            // Not enabled, cannot load.
            if (context.testPermission(WorldPermissions.BASE_WORLD_ENABLE)) {
                return context.errorResult("command.world.load.notenabled.enable", worldProperties.getKey().asString());
            }

            return context.errorResult("command.world.load.notenabled.noenable", worldProperties.getKey().asString());
        }

        if (Sponge.getServer().getWorldManager().getWorld(worldProperties.getKey()).isPresent()) {
            return context.errorResult("command.world.load.alreadyloaded", worldProperties.getKey().asString());
        }

        worldProperties.setEnabled(true);
        context.sendMessage("command.world.load.start", worldProperties.getKey().asString());
        Sponge.getServer().getWorldManager().loadWorld(worldProperties).handle((world, exception) -> {
            if (exception != null) {
                context.getServiceCollection().schedulerService().runOnMainThread(() ->
                    context.sendMessage("command.world.load.fail", worldProperties.getKey().asString()));
            } else {
                context.getServiceCollection().schedulerService().runOnMainThread(() ->
                        context.sendMessage("command.world.load.loaded", worldProperties.getKey().asString()));
            }
            return null;
        });
        return context.successResult();
    }


}
