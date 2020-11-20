/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ConfigSerializable
public final class MuteData {

    @Setting
    private UUID muter;

    @Setting
    private String reason;

    @Setting
    private long creationTime;

    @Setting
    private Long absoluteTime;

    @Setting
    private Long timeFromNextLogin;

    // For Configurate
    public MuteData() { }

    public MuteData(@Nullable final UUID muter, final String reason, @Nullable final Instant creationInstant, @Nullable final Duration timeFromNextLogin,
            @Nullable final Instant endtimestamp) {
        this.muter = muter;
        this.reason = reason;
        if (creationInstant != null) {
            this.creationTime = creationInstant.getEpochSecond();
        }
        if (timeFromNextLogin != null) {
            this.timeFromNextLogin = timeFromNextLogin.getSeconds();
        } else if (endtimestamp != null) {
            this.absoluteTime = endtimestamp.getEpochSecond();
        }
    }

    public UUID getMuter() {
        return this.muter;
    }

    public String getReason() {
        return this.reason;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public Long getAbsoluteTime() {
        return this.absoluteTime;
    }

    public Long getTimeFromNextLogin() {
        return this.timeFromNextLogin;
    }
}
