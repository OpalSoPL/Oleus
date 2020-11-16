/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.data;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jailing;
import io.github.nucleuspowered.nucleus.datatypes.EndTimestamp;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public final class JailData extends EndTimestamp implements Jailing {

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

    // Configurate
    public JailData() { }

    public JailData(final UUID jailer, final String jailName, final String reason, final ServerLocation previousLocation) {
        this.jailer = jailer;
        this.reason = reason;
        this.jailName = jailName;

        if (previousLocation != null) {
            this.worldKey = previousLocation.getWorldKey();
            this.previousx = previousLocation.getX();
            this.previousy = previousLocation.getY();
            this.previousz = previousLocation.getZ();
        }
    }

    public JailData(final UUID jailer, final String jailName, final String reason, final ServerLocation previousLocation, final Instant endTimestamp) {
        this(jailer, jailName, reason, previousLocation);
        this.endtimestamp = endTimestamp.getEpochSecond();
    }

    public JailData(final UUID jailer, final String jailName, final String reason, final ServerLocation previousLocation, final Duration timeFromNextLogin) {
        this(jailer, jailName, reason, previousLocation);
        this.timeFromNextLogin = timeFromNextLogin.getSeconds();
    }

    public void setPreviousLocation(final ServerLocation previousLocation) {
        this.worldKey = previousLocation.getWorldKey();
        this.previousx = previousLocation.getX();
        this.previousy = previousLocation.getY();
        this.previousz = previousLocation.getZ();
    }

    @Override public String getReason() {
        return this.reason;
    }

    @Override public String getJailName() {
        return this.jailName;
    }

    @Override public Optional<UUID> getJailer() {
        return this.jailer.equals(Util.CONSOLE_FAKE_UUID) ? Optional.empty() : Optional.of(this.jailer);
    }

    public Optional<Instant> getCreationInstant() {
        return this.creationTime > 0 ? Optional.of(Instant.ofEpochSecond(this.creationTime)) : Optional.empty();
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public UUID getJailerInternal() {
        return this.jailer;
    }

    @Override
    public Optional<ServerLocation> getPreviousLocation() {
        if (this.worldKey != null) {
            final Optional<ServerWorld> ow = Sponge.getServer().getWorldManager().getWorld(this.worldKey);
            if (ow.isPresent() && this.previousx != 0 && this.previousy != -1 && this.previousz != 0) {
                return Optional.of(ServerLocation.of(ow.get(), this.previousx, this.previousy, this.previousz));
            }
        }

        return Optional.empty();
    }
}
