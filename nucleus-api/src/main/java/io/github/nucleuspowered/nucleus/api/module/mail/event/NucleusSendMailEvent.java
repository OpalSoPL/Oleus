/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.mail.event;

import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Optional;
import java.util.UUID;

/**
 * Fired when a player sends a mail message.
 */
@MightOccurAsync
public interface NucleusSendMailEvent extends Event, Cancellable {
    /**
     * The sender of the mail. If {@link Optional#empty()}, this means it was
     * some server process.
     *
     * @return The sender
     */
    Optional<UUID> getSender();

    /**
     * The recipient of the mail.
     *
     * @return The recipient.
     */
    UUID getRecipient();

    /**
     * The message that was sent.
     *
     * @return The message.
     */
    String getMessage();
}
