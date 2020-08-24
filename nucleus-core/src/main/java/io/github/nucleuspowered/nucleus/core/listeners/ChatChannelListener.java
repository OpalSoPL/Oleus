/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.listeners;

import com.google.common.collect.ImmutableList;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IChatMessageFormatterService;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.util.Optional;
import java.util.UUID;

public class ChatChannelListener implements ListenerBase {

    private final IChatMessageFormatterService chatMessageFormatter;

    public ChatChannelListener(final INucleusServiceCollection serviceCollection) {
        this.chatMessageFormatter = serviceCollection.chatMessageFormatter();
    }

    @Listener(order = Order.LATE)
    public void onChatMessageLast(final MessageChannelEvent chat, @Root final SystemSubject source) {
        final Optional<IChatMessageFormatterService.Channel> channelOptional =
                this.chatMessageFormatter.getNucleusChannel(Util.CONSOLE_FAKE_UUID);
        channelOptional.ifPresent(x -> this.onChatChannel(chat, x));
    }

    // Used to perform any transformations so that they can be caught by other plugins.
    @Listener(order = Order.LATE)
    public void onChatMessageLast(final MessageChannelEvent chat, @Root final Player source) {
        final Optional<IChatMessageFormatterService.Channel> channelOptional =
                this.chatMessageFormatter.getNucleusChannel(source.getUniqueId());
        channelOptional.ifPresent(x -> this.onChatChannel(chat, x));

    }

    private void onChatChannel(final MessageChannelEvent chat, final IChatMessageFormatterService.Channel channel) {
        if (channel.willFormat()) {
            channel.formatMessageEvent(source, chat.getFormatter());
            chat.setChannel(chat.getChannel().map(x -> {
                final MutableMessageChannel messageChannel = x.asMutable();
                // Copy to make sure we don't CME
                for (final MessageReceiver toSendTo : ImmutableList.copyOf(messageChannel.getMembers())) {
                    if (!channel.receivers().contains(toSendTo)) {
                        // If the receiver is not in the channel, remove
                        messageChannel.removeMember(toSendTo);
                    }
                }

                return (MessageChannel) messageChannel;
            }).orElseGet(() -> MessageChannel.fixed(channel.receivers())));
        }
    }

}
