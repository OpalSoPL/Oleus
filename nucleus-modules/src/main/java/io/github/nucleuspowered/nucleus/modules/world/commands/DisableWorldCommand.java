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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = {"disable", "dis"},
        basePermission = WorldPermissions.BASE_WORLD_DISABLE,
        commandDescriptionKey = "world.disable",
        parentCommand = WorldCommand.class
)
public class DisableWorldCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties worldProperties = context.requireOne(NucleusParameters.Keys.WORLD, WorldProperties.class);
        if (!worldProperties.isEnabled()) {
            return context.errorResult("command.world.disable.alreadydisabled", worldProperties.getWorldName());
        }

        if (Sponge.getServer().getWorld(worldProperties.getUniqueId()).isPresent()) {
            return context.errorResult("command.world.disable.warnloaded", worldProperties.getWorldName());
        }

        return disableWorld(context, worldProperties);
    }

    static ICommandResult disableWorld(final ICommandContext context, final WorldProperties worldProperties) {
        worldProperties.setEnabled(false);
        if (worldProperties.isEnabled()) {
            return context.errorResult("command.world.disable.couldnotdisable", worldProperties.getWorldName());
        }

        context.sendMessage("command.world.disable.success", worldProperties.getWorldName());
        return context.successResult();
    }
}
