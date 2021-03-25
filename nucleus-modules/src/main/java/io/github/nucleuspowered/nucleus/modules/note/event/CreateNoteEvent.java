/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.event;

import io.github.nucleuspowered.nucleus.api.module.note.event.NucleusNoteEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class CreateNoteEvent implements NucleusNoteEvent.Created {

    @Nullable private final UUID author;
    private final String note;
    private final UUID targetUser;
    private final Cause cause;
    private final Instant instant;

    public CreateNoteEvent(@Nullable final UUID author, final String note, final Instant date, final UUID targetUser, final Cause cause) {
        this.author = author;
        this.note = note;
        this.instant = date;
        this.targetUser = targetUser;
        this.cause = cause;
    }

    @Override
    public Optional<UUID> getAuthor() {
        return Optional.ofNullable(this.author);
    }

    @Override
    public Instant getDate() {
        return this.instant;
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public UUID getTargetUser() {
        return this.targetUser;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }
}
