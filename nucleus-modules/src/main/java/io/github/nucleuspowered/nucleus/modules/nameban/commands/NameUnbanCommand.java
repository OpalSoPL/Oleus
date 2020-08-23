/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nameban.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.nameban.exception.NameBanException;
import io.github.nucleuspowered.nucleus.modules.nameban.NameBanPermissions;
import io.github.nucleuspowered.nucleus.modules.nameban.services.NameBanHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.RegexArgument;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.text.Text;
@Command(
        aliases = {"nameunban", "namepardon"},
        basePermission = NameBanPermissions.BASE_NAMEUNBAN,
        commandDescriptionKey = "nameunban",
        async = true)
public class NameUnbanCommand implements ICommandExecutor {

    private final String nameKey = "name";

    @Override public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            new RegexArgument(Text.of(this.nameKey), Util.usernameRegexPattern, "command.nameban.notvalid", serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String name = context.requireOne(this.nameKey, String.class).toLowerCase();

        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(context.getCommandSourceRoot());
            context.getServiceCollection().getServiceUnchecked(NameBanHandler.class).removeName(name, frame.getCurrentCause());
            context.sendMessage("command.nameban.pardon.success", name);
            return context.successResult();
        } catch (final NameBanException ex) {
            ex.printStackTrace();
            return context.errorResult("command.nameban.pardon.failed", name);
        }
    }
}
