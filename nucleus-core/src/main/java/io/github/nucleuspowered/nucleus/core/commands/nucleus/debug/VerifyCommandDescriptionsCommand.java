/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus.debug;

import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.DebugCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.interfaces.ICommandMetadataService;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;

import java.util.List;
import java.util.stream.Collectors;

@Command(
        aliases = "verifycmds",
        basePermission = CorePermissions.BASE_NUCLEUS_DEBUG_VERIFY,
        commandDescriptionKey = "nucleus.debug.verifycmds",
        parentCommand = DebugCommand.class
)
public class VerifyCommandDescriptionsCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ICommandMetadataService commandMetadataService = context.getServiceCollection().commandMetadataService();
        final IMessageProviderService messageProviderService = context.getServiceCollection().messageProvider();
        final List<Component> messages = commandMetadataService.getCommands()
                .stream()
                .filter(x -> {
                    final String key = x.getMetadata().getCommandAnnotation().commandDescriptionKey() + ".desc";
                    return !messageProviderService.hasKey(key);
                })
                .map(x -> Component.text().content("Command /" + x.getCommand() +
                        " missing key \"" + x.getMetadata().getCommandAnnotation().commandDescriptionKey() + ".desc\"").build())
                .collect(Collectors.toList());

        if (messages.isEmpty()) {
            context.sendMessageText(Component.text("All commands have valid description keys."));
        } else {
            context.sendMessageText(Component.text("Some commands do not have valid description keys:"));
            messages.forEach(context::sendMessageText);
        }

        return context.successResult();
    }
}
