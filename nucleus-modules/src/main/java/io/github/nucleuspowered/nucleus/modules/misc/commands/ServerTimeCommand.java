/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.modules.misc.MiscPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Command(aliases = { "servertime", "realtime" }, basePermission = MiscPermissions.BASE_SERVERTIME, commandDescriptionKey = "servertime")
public class ServerTimeCommand implements ICommandExecutor {

    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        context.sendMessage("command.servertime.time", DATE_TIME_FORMAT.format(LocalDateTime.now()));
        return context.successResult();
    }
}
