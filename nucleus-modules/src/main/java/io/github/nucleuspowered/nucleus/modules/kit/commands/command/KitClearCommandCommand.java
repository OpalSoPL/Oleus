/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = { "clear" },
        basePermission = KitPermissions.BASE_KIT_COMMAND_REMOVE,
        commandDescriptionKey = "kit.command.clear",
        parentCommand = KitCommandCommand.class
)
public class KitClearCommandCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission()
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kitInfo = context.requireOne(KitService.KIT_KEY);
        kitInfo.setCommands(new ArrayList<>());
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kitInfo);

        context.sendMessage("command.kit.command.clear.command", kitInfo.getName());
        return context.successResult();
    }
}
