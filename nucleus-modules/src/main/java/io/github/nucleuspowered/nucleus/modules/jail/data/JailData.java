/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ConfigSerializable
public final class JailData {

    @Setting
    private UUID jailer;

    @Setting
    private String jailName;

    @Setting
    private String reason;

    @Setting
    private double previousx = 0;

    @Setting
    private double previousy = -1;

    @Setting
    private double previousz = 0;

    @Setting
    private ResourceKey worldKey;

    @Setting
    private long creationTime = Instant.now().getEpochSecond();

    @Setting
    private Long absoluteTime;

    @Setting
    private Long timeFromNextLogin;

    // Configurate
    public JailData() { }

    public JailData(
            final UUID jailer,
            final String jailName,
            final String reason,
            @Nullable final Instant creationTime,
            @Nullable final ServerLocation previousLocation,
            @Nullable final Instant absoluteTime,
            @Nullable final Duration timeFromNextLogin) {
        this.jailer = jailer;
        this.reason = reason;
        this.jailName = jailName;

        if (previousLocation != null) {
            this.worldKey = previousLocation.worldKey();
            this.previousx = previousLocation.getX();
            this.previousy = previousLocation.getY();
            this.previousz = previousLocation.getZ();
        }
        this.creationTime = creationTime == null ? Instant.now().getEpochSecond() : creationTime.getEpochSecond();
        this.absoluteTime = absoluteTime == null ? null : absoluteTime.getEpochSecond();
        this.timeFromNextLogin = timeFromNextLogin == null ? null : timeFromNextLogin.getSeconds();
    }

    public UUID getJailer() {
        return this.jailer;
    }

    public String getJailName() {
        return this.jailName;
    }

    public String getReason() {
        return this.reason;
    }

    public double getPreviousx() {
        return this.previousx;
    }

    public double getPreviousy() {
        return this.previousy;
    }

    public double getPreviousz() {
        return this.previousz;
    }

    public ResourceKey worldKey() {
        return this.worldKey;
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
