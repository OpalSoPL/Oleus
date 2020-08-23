/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.parameters.KitParameter;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
@Command(
        aliases = { "add", "+" },
        basePermission = KitPermissions.BASE_KIT_COMMAND_ADD,
        commandDescriptionKey = "kit.command.add",
        async = true,
        parentCommand = KitCommandCommand.class
)
public class KitAddCommandCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.getServiceUnchecked(KitService.class).createKitElement(false),
                NucleusParameters.COMMAND
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kitInfo = context.requireOne(KitParameter.KIT_PARAMETER_KEY, Kit.class);
        final String c = context.requireOne(NucleusParameters.Keys.COMMAND, String.class)
                .replace(" {player} ", " {{player}} ");
        kitInfo.addCommand(c);
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kitInfo);

        context.sendMessage("command.kit.command.add.command", c, kitInfo.getName());
        return context.successResult();
    }
}
