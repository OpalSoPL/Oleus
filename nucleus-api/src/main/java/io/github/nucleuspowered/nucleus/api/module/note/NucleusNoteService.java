/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.note;

import io.github.nucleuspowered.nucleus.api.module.note.data.Note;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A service that determines whether a subject has notes.
 */
public interface NucleusNoteService {

    /**
     * Gets all notes for a specific user
     *
     * @param user The {@link UUID} to check.
     * @return A list of {@link Note}s.
     */
    CompletableFuture<Collection<Note>> getNotes(UUID user);

    /**
     * Adds a note to a subject.
     *
     * @param user The {@link UUID} of a user to add a note to.
     * @param note The note to add.
     * @return A {@link CompletableFuture} indicating success.
     */
    CompletableFuture<Boolean> addNote(UUID user, String note);

    /**
     * Removes a note from a subject.
     *
     * @param user The {@link UUID} of a user to remove a note from.
     * @param note The {@link Note} to remove.
     * @return A {@link CompletableFuture} indicating success.
     */
    CompletableFuture<Boolean> removeNote(UUID user, Note note);

    /**
     * Clears all notes from a subject.
     *
     * @param user The {@link UUID} of the user to remove all notes from.
     * @return A {@link CompletableFuture} indicating success.
     */
    CompletableFuture<Boolean> clearNotes(UUID user);
}
