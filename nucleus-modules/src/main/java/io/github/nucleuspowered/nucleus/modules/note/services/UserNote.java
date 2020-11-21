/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.services;

import io.github.nucleuspowered.nucleus.api.module.note.data.Note;
import io.github.nucleuspowered.nucleus.modules.note.data.NoteData;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class UserNote implements Note {

    public static UserNote fromNoteData(final NoteData noteData) {
        return new UserNote(noteData.getNoter(), noteData.getNote(), Instant.ofEpochMilli(noteData.getDate()));
    }

    @Nullable private final UUID noter;
    private final String note;
    private final Instant date;

    public UserNote(@Nullable final UUID noter, final String note, final Instant date) {
        this.noter = noter;
        this.note = note;
        this.date = date;
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public Optional<UUID> getNoter() {
        return Optional.ofNullable(this.noter);
    }

    @Override
    public Instant getDate() {
        return this.date;
    }

    public NoteData toNoteData() {
        return new NoteData(this.date.toEpochMilli(), this.noter, this.note);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final UserNote userNote = (UserNote) o;
        return Objects.equals(this.noter, userNote.noter) &&
                Objects.equals(this.note, userNote.note) &&
                Objects.equals(this.date, userNote.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.noter, this.note, this.date);
    }
}
