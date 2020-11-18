/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.commands;

import io.github.nucleuspowered.nucleus.modules.mail.MailPermissions;
import io.github.nucleuspowered.nucleus.modules.mail.services.MailHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@EssentialsEquivalent({"mail", "email"})
@Command(
        aliases = { "mail", "email" },
        basePermission = MailPermissions.BASE_MAIL,
        commandDescriptionKey = "mail")
public class MailCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(MailHandler.class).getMailFilterParameter()
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        return MailReadBase.INSTANCE.executeCommand(
                context,
                context.requirePlayer().getUniqueId(),
                context.getAll(context.getServiceCollection().getServiceUnchecked(MailHandler.class).getMailFilterParameter()));
    }
}
