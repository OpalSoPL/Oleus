/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.commands;

import io.github.nucleuspowered.nucleus.api.module.mail.NucleusMailService;
import io.github.nucleuspowered.nucleus.modules.mail.MailPermissions;
import io.github.nucleuspowered.nucleus.modules.mail.parameter.MailFilterArgument;
import io.github.nucleuspowered.nucleus.modules.mail.services.MailHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
@Command(
        aliases = { "other", "o" },
        basePermission = MailPermissions.BASE_MAIL_OTHER,
        commandDescriptionKey = "mail.other",
        async = true,
        parentCommand = MailCommand.class
)
public class MailOtherCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.ONE_USER.get(serviceCollection),
                GenericArguments.optional(GenericArguments.allOf(new MailFilterArgument(Text.of(MailReadBase.FILTERS), serviceCollection.getServiceUnchecked(
                        MailHandler.class))))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        return MailReadBase.INSTANCE.executeCommand(
                context,
                context.requireOne(NucleusParameters.Keys.USER, User.class),
                context.getAll(MailReadBase.FILTERS, NucleusMailService.MailFilter.class));
    }
}
