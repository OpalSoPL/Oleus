/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.services;

import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

// Managing teleport requests.
public final class TeleportRequest extends TeleportTask {

    private final Instant expiry;
    private boolean forcedExpired;
    private boolean expired;

    public TeleportRequest(
            final INucleusServiceCollection serviceCollection,
            final UUID toTeleport,
            final UUID target,
            final Instant expiry,
            final double cost,
            final int warmup,
            @Nullable final UUID requester,
            final boolean safe,
            final boolean silentTarget,
            final boolean silentSource,
            @Nullable final ServerLocation requestLocation,
            @Nullable final Vector3d rotation,
            @Nullable final Consumer<Player> successCallback) {
        super(serviceCollection, toTeleport, target, cost, warmup, safe, silentSource, silentTarget, requestLocation, rotation, requester, successCallback);
        this.expiry = expiry;
    }

    public Optional<ServerPlayer> getToBeTeleported() {
        return Sponge.server().getPlayer(this.toTeleport);
    }

    public Optional<ServerPlayer> getTarget() {
        return Sponge.server().getPlayer(this.target);
    }

    public void forceExpire(final boolean callback) {
        if (!callback || this.isActive()) {
            this.forcedExpired = true;
            if (callback) {
                this.onCancel();
            }
        }
    }

    public boolean isActive() {
        if (!this.expired) {
            this.expired = (this.forcedExpired && Instant.now().isAfter(this.expiry));
            if (this.expired) {
                this.onCancel();
            }
        }

        return !this.forcedExpired;
    }

    public Instant getExpiryTime() {
        return this.expiry;
    }

}
