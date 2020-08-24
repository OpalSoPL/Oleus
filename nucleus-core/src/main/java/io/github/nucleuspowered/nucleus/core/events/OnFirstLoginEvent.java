/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.events;

import io.github.nucleuspowered.nucleus.api.core.event.NucleusFirstJoinEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import java.util.Optional;

import javax.annotation.Nullable;

public class OnFirstLoginEvent extends AbstractEvent implements NucleusFirstJoinEvent {

    private final Cause cause;
    private final Player player;
    private final MessageChannel originalChannel;
    private final TextComponent originalMessage;
    private final MessageFormatter formatter;
    @Nullable private MessageChannel currentChannel;
    private boolean cancelled;

    public OnFirstLoginEvent(final Cause cause, final Player player, final MessageChannel originalChannel,
        @Nullable final MessageChannel currentChannel, final TextComponent originalMessage, final boolean messageCancelled, final MessageFormatter formatter) {

        this.cause = cause;
        this.player = player;
        this.originalChannel = originalChannel;
        this.originalMessage = originalMessage;
        this.cancelled = messageCancelled;
        this.formatter = formatter;
        this.currentChannel = currentChannel;
    }

    @Override public Player getTargetEntity() {
        return this.player;
    }

    @Override public Cause getCause() {
        return this.cause;
    }

    @Override public MessageChannel getOriginalChannel() {
        return this.originalChannel;
    }

    @Override public Optional<MessageChannel> getChannel() {
        return Optional.ofNullable(this.currentChannel);
    }

    @Override public void setChannel(@Nullable final MessageChannel channel) {
        this.currentChannel = channel;
    }

    @Override public TextComponent getOriginalMessage() {
        return this.originalMessage;
    }

    @Override public boolean isMessageCancelled() {
        return this.cancelled;
    }

    @Override public void setMessageCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override public MessageFormatter getFormatter() {
        return this.formatter;
    }
}
