/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.spongepowered.api.command.exception.CommandException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(aliases = "checkmuted", basePermission = MutePermissions.BASE_CHECKMUTED, commandDescriptionKey = "checkmuted")
public class CheckMutedCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {

        // Using the cache, tell us who is jailed.
        final List<UUID> usersInMute = context.getServiceCollection().userCacheService().getMuted();

        if (usersInMute.isEmpty()) {
            context.sendMessage("command.checkmuted.none");
            return context.successResult();
        }

        // Get the users in this jail, or all jails
        Util.getPaginationBuilder(context.audience())
            .title(context.getMessage("command.checkmuted.header"))
            .contents(usersInMute.stream().map(x -> {
                final Component name = context.getServiceCollection().playerDisplayNameService().getDisplayName(x);
                return name
                    .hoverEvent(HoverEvent.showText(context.getMessage("command.checkmuted.hover")))
                    .clickEvent(ClickEvent.runCommand("/nucleus:checkmute " + x.toString()));
            }).collect(Collectors.toList())).sendTo(context.audience());
        return context.successResult();
    }
}
