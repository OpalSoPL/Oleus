/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.note;

import com.google.common.collect.ImmutableList;
import io.github.nucleuspowered.nucleus.api.module.note.data.Note;

import java.util.UUID;

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
    ImmutableList<Note> getNotes(UUID user);

    /**
     * Adds a note to a subject.
     *
     * @param user The {@link UUID} of a user to add a note to.
     * @param note The note to add.
     * @return <code>true</code> if the note was added.
     */
    boolean addNote(UUID user, String note);

    /**
     * Removes a note from a subject.
     *
     * @param user The {@link UUID} of a user to remove a note from.
     * @param note The {@link Note} to remove.
     * @return <code>true</code> if the note was removed.
     */
    boolean removeNote(UUID user, Note note);

    /**
     * Clears all notes from a subject.
     *
     * @param user The {@link UUID} of the user to remove all notes from.
     * @return <code>true</code> if all notes were removed.
     */
    boolean clearNotes(UUID user);
}
