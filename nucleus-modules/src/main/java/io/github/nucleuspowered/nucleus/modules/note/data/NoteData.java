/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.UUID;

@ConfigSerializable
public class NoteData {

    @Setting
    @Nullable
    private UUID noter;

    @Setting
    private String note;

    @Setting
    private long date;

    public NoteData() { }

    public NoteData(final long date, @Nullable final UUID noter, final String note) {
        this.noter = noter;
        this.note = note;
        this.date = date;
    }

    public UUID getNoter() {
        return this.noter;
    }

    public String getNote() {
        return this.note;
    }

    public long getDate() {
        return this.date;
    }

}
