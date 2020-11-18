/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.data;

import io.github.nucleuspowered.nucleus.api.module.mail.data.MailMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class MailData implements MailMessage {

    @Setting
    @Nullable
    private UUID uuid;

    @Setting
    private long date;

    @Setting
    private String message;

    public MailData() { }

    public MailData(final UUID uuid, final Instant date, final String message) {
        this.uuid = uuid;
        this.date = date.toEpochMilli();
        this.message = message;
    }

    @Override public String getMessage() {
        return this.message;
    }

    @Override public Instant getDate() {
        return Instant.ofEpochMilli(this.date);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override public Optional<UUID> getSender() {
        return Optional.ofNullable(this.uuid);
    }

    @Override public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final MailData mailData = (MailData) o;

        if (this.date != mailData.date) {
            return false;
        }
        if (this.uuid == null) {
            return mailData.uuid == null;
        }
        return this.uuid.equals(mailData.uuid) && this.message.equals(mailData.message);
    }

    @Override public int hashCode() {
        int result = this.message.hashCode();
        result = 31 * result + (int) (this.date ^ (this.date >>> 32));
        if (this.uuid != null) {
            result = 31 * result + this.uuid.hashCode();
        }
        return result;
    }
}
