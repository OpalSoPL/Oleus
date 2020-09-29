/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus;

import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.ConfigFileMessagesRepository;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.managed.Flag;

import java.util.List;

@Command(
        aliases = "update-messages",
        basePermission = CorePermissions.BASE_NUCLEUS_UPDATE_MESSAGES,
        commandDescriptionKey = "nucleus.update-messages",
        async = true
)
public class MessagesUpdateCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("y")
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // First, reload the messages.
        final IMessageProviderService messageProviderService = context.getServiceCollection().messageProvider();
        final boolean reload = messageProviderService.reloadMessageFile(); //Nucleus.getNucleus().reloadMessages();
        if (!reload) { // only false if we can't read the custom messages file.
            // There was a failure loading a custom file
            context.errorResult("command.nucleus.messageupdate.notfile");
        }

        final ConfigFileMessagesRepository messagesRepository = messageProviderService.getConfigFileMessageRepository();
        final List<String> mismatched = messagesRepository.walkThroughForMismatched();
        context.sendMessage("command.nucleus.messageupdate.reloaded");
        if (mismatched.isEmpty()) {
            return context.successResult();
        }

        if (context.hasFlag("y")) {
            messagesRepository.fixMismatched(mismatched);
            context.sendMessage("command.nucleus.messageupdate.reset");
        } else {
            context.sendMessage("command.nucleus.messageupdate.sometoupdate", String.valueOf(mismatched.size()));
            mismatched.forEach(x -> context.sendMessageText(Component.text(x, NamedTextColor.YELLOW)));
            messageProviderService
                    .getMessageFor(context.getAudience(),
                            "command.nucleus.messageupdate.confirm",
                            Component.text().content("/nucleus update-messages -y")
                            .clickEvent(ClickEvent.runCommand("/nucleus update-messages -y")).build());
        }

        return context.successResult();
    }
}
