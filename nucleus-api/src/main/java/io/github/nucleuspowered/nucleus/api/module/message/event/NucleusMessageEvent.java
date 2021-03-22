/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message.event;

import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.UserMessageTarget;
import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Optional;
import java.util.UUID;

/**
 * An event that is posted when a player sends a private message.
 */
@MightOccurAsync
public interface NucleusMessageEvent extends Event, Cancellable {

    /**
     * The sender.
     *
     * @return The {@link MessageTarget} of the user that sent the message.
     */
    MessageTarget getSender();

    /**
     * The sender, if it is a player.
     *
     * @return The sender as a player.
     */
    default Optional<ServerPlayer> getSenderAsPlayer() {
        if (this.getSender() instanceof UserMessageTarget) {
            return Sponge.server().player(((UserMessageTarget) this.getSender()).getUserUUID());
        }
        return Optional.empty();
    }

    /**
     * The recipient.
     *
     * @return The {@link UUID} that receives the message, or
     *  {@link Optional#empty()} for the {@link SystemSubject}.
     */
    MessageTarget getReceiver();

    /**
     * The receiver, if it is a player.
     *
     * @return The reciever as a player.
     */
    default Optional<ServerPlayer> getReceiverAsPlayer() {
        if (this.getReceiver() instanceof UserMessageTarget) {
            return Sponge.server().player(((UserMessageTarget) this.getReceiver()).getUserUUID());
        }
        return Optional.empty();
    }

    /**
     * The message that was sent.
     *
     * @return The message.
     */
    String getMessage();
}
