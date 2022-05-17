/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfig;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = { "add", "+" },
        basePermission = KitPermissions.BASE_KIT_COMMAND_ADD,
        commandDescriptionKey = "kit.command.add",
        parentCommand = KitCommandCommand.class
)
public class KitAddCommandCommand extends KitCommandCommandBase {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission(),
                NucleusParameters.COMMAND
        };
    }

    @Override protected ICommandResult execute0(final ICommandContext context) throws CommandException {
        final Kit kitInfo = context.requireOne(KitService.KIT_KEY);
        final String c = context.requireOne(NucleusParameters.COMMAND)
                .replace(" {player} ", " {{player}} ");
        kitInfo.addCommand(c);
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kitInfo);

        context.sendMessage("command.kit.command.add.command", c, kitInfo.getName());
        return context.successResult();
    }
}
