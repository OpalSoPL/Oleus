/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.events;

import io.github.nucleuspowered.nucleus.api.core.event.NucleusFirstJoinEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;

public class OnFirstLoginEvent extends AbstractEvent implements NucleusFirstJoinEvent {

    private final Cause cause;
    private final ServerPlayer player;
    private final Audience originalChannel;
    private final Component originalMessage;
    @Nullable private Audience currentChannel;
    private Component message;
    private boolean cancelled;

    public OnFirstLoginEvent(final Cause cause, final ServerPlayer player, final Audience originalChannel,
        @Nullable final Audience currentChannel, final Component originalMessage, final boolean messageCancelled) {

        this.cause = cause;
        this.player = player;
        this.originalChannel = originalChannel;
        this.originalMessage = originalMessage;
        this.message = originalMessage;
        this.cancelled = messageCancelled;
        this.currentChannel = currentChannel;
    }

    @Override public ServerPlayer getPlayer() {
        return this.player;
    }

    @Override public Cause getCause() {
        return this.cause;
    }

    @Override public Component getOriginalMessage() {
        return this.originalMessage;
    }

    @Override public Component getMessage() {
        return this.message;
    }

    @Override public void setMessage(final Component message) {
        this.message = message;
    }

    @Override public Audience getOriginalAudience() {
        return this.originalChannel;
    }

    @Override public Optional<Audience> getAudience() {
        return Optional.ofNullable(this.currentChannel);
    }

    @Override public void setAudience(@Nullable final Audience audience) {
        this.currentChannel = audience;
    }

    @Override
    public boolean isMessageCancelled() {
        return this.cancelled;
    }
}
