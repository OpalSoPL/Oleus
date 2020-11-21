/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.note.event;

import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import org.spongepowered.api.event.Event;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Events for when notes are added to users.
 */
@MightOccurAsync
public interface NucleusNoteEvent extends Event {

    /**
     * Gets the {@link UUID} of the player who added the note,
     * or an {@link Optional#empty()} if it was not a player.
     *
     * @return The {@link UUID} of the player, if any.
     */
    Optional<UUID> getAuthor();

    /**
     * Gets the {@link Instant} the note was created.
     *
     * @return When the note was created.
     */
    Instant getDate();

    /**
     * The note.
     *
     * @return The note
     */
    String getNote();

    /**
     * The subject of the note.
     *
     * @return The {@link UUID} of the user
     */
    UUID getTargetUser();

    /**
     * Event that is fired when a note is created.
     */
    interface Created extends NucleusNoteEvent {}
}
