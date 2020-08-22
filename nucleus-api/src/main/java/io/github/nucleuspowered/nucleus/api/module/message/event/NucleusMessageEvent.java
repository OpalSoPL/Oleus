/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message.event;

import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import org.spongepowered.api.SystemSubject;
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
     * @return The {@link UUID} of the user that sent the message,
     *  or {@link Optional#empty()} for the {@link SystemSubject}.
     */
    Optional<UUID> getSender();

    /**
     * The recipient.
     *
     * @return The {@link UUID} that receives the message, or
     *  {@link Optional#empty()} for the {@link SystemSubject}.
     */
    Optional<UUID> getRecipient();

    /**
     * The message that was sent.
     *
     * @return The message.
     */
    String getMessage();
}
