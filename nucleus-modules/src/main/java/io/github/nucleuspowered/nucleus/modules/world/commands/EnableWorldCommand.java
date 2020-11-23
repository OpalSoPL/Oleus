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
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = {"enable", "en"},
        basePermission = WorldPermissions.BASE_WORLD_ENABLE,
        commandDescriptionKey = "world.enable",
        parentCommand = WorldCommand.class
)
public class EnableWorldCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.WORLD_PROPERTIES_DISABLED_ONLY
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties worldProperties = context.requireOne(NucleusParameters.WORLD_PROPERTIES_DISABLED_ONLY);
        if (worldProperties.isEnabled()) {
            return context.errorResult("command.world.enable.alreadyenabled", worldProperties.getKey().asString());
        }

        worldProperties.setEnabled(true);
        context.sendMessage("command.world.enable.success", worldProperties.getKey().asString());
        return context.successResult();
    }
}
