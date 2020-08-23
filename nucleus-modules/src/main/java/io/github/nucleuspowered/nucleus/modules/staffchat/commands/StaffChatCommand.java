/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.commands;

import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.util.NoExceptionAutoClosable;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatMessageChannel;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatPermissions;
import io.github.nucleuspowered.nucleus.modules.staffchat.services.StaffChatService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IChatMessageFormatterService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import java.util.Optional;

import com.google.inject.Inject;

@Command(
        aliases = {"staffchat", "sc", "a"},
        basePermission = StaffChatPermissions.BASE_STAFFCHAT,
        commandDescriptionKey = "staffchat"
)
public class StaffChatCommand implements ICommandExecutor {

    private final IChatMessageFormatterService chatMessageFormatterService;

    @Inject
    public StaffChatCommand(final INucleusServiceCollection serviceCollection) {
        this.chatMessageFormatterService = serviceCollection.chatMessageFormatter();
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<String> toSend = context.getOne(NucleusParameters.Keys.MESSAGE, String.class);
        if (toSend.isPresent()) {
            try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                frame.addContext(EventContexts.SHOULD_FORMAT_CHANNEL, StaffChatMessageChannel.getInstance().formatMessages());
                if (context.is(Player.class)) {
                    final Player pl = context.getIfPlayer();
                    frame.pushCause(pl);
                    frame.addContext(EventContextKeys.PLAYER_SIMULATED, pl.getProfile());

                    try (final NoExceptionAutoClosable c = this.chatMessageFormatterService
                            .setPlayerNucleusChannelTemporarily(pl.getUniqueId(), StaffChatMessageChannel.getInstance())) {
                        pl.simulateChat(
                                context.getServiceCollection()
                                        .textStyleService()
                                        .addUrls(toSend.get()), Sponge.getCauseStackManager().getCurrentCause());
                    }

                    // If you send a message, you're viewing it again.
                    context.getServiceCollection()
                            .userPreferenceService()
                            .setPreferenceFor(pl, NucleusKeysProvider.VIEW_STAFF_CHAT, true);
                } else {
                    StaffChatMessageChannel.getInstance()
                            .sendMessageFrom(context.getCommandSourceRoot(),
                                    context.getServiceCollection().textStyleService().addUrls(toSend.get()));
                }

                return context.successResult();
            }
        }

        if (!(context.is(Player.class))) {
            return context.errorResult("command.staffchat.consoletoggle");
        }

        final Player player = context.getIfPlayer();

        final StaffChatService service = context.getServiceCollection().getServiceUnchecked(StaffChatService.class);
        final boolean result = service.isToggledChat(player);
        service.toggle(player, !result);

        context.sendMessage("command.staffchat." + (!result ? "on" : "off"));
        return context.successResult();
    }

}
