/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.commands;

import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.util.NoExceptionAutoClosable;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatKeys;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatMessageChannel;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatPermissions;
import io.github.nucleuspowered.nucleus.modules.staffchat.services.StaffChatService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IChatMessageFormatterService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.CauseStackManager;
import java.util.Optional;

import com.google.inject.Inject;
import org.spongepowered.api.event.EventContextKeys;

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
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<String> toSend = context.getOne(NucleusParameters.OPTIONAL_MESSAGE);
        if (toSend.isPresent()) {
            try (final CauseStackManager.StackFrame frame = Sponge.server().causeStackManager().pushCauseFrame()) {
                frame.addContext(EventContexts.SHOULD_FORMAT_CHANNEL, StaffChatMessageChannel.getInstance().formatMessages());
                if (context.is(ServerPlayer.class)) {
                    final ServerPlayer pl = context.requirePlayer();
                    frame.pushCause(pl);
                    frame.addContext(EventContextKeys.SIMULATED_PLAYER, pl.getProfile());

                    try (final NoExceptionAutoClosable c = this.chatMessageFormatterService
                            .setPlayerNucleusChannelTemporarily(pl.uniqueId(), StaffChatMessageChannel.getInstance())) {
                        pl.simulateChat(
                                context.getServiceCollection()
                                        .textStyleService()
                                        .addUrls(toSend.get()), Sponge.server().causeStackManager().currentCause());
                    }

                    // If you send a message, you're viewing it again.
                    context.getServiceCollection()
                            .userPreferenceService()
                            .setPreferenceFor(pl.uniqueId(), StaffChatKeys.VIEW_STAFF_CHAT, true);
                } else {
                    // mostly to allow plugins to know that's what we're doing.
                    try (final NoExceptionAutoClosable c = this.chatMessageFormatterService
                            .setAudienceNucleusChannelTemporarily(context.audience(), StaffChatMessageChannel.getInstance())) {
                        StaffChatMessageChannel.getInstance()
                                .sendMessageFrom(context.audience(),
                                        context.getServiceCollection().textStyleService().addUrls(toSend.get()));
                    }
                }

                return context.successResult();
            }
        }

        if (!(context.is(ServerPlayer.class))) {
            return context.errorResult("command.staffchat.consoletoggle");
        }

        final ServerPlayer player = context.getIfPlayer();

        final StaffChatService service = context.getServiceCollection().getServiceUnchecked(StaffChatService.class);
        final boolean result = service.isToggledChat(player);
        service.toggle(player, !result);

        context.sendMessage("command.staffchat." + (!result ? "on" : "off"));
        return context.successResult();
    }

}
