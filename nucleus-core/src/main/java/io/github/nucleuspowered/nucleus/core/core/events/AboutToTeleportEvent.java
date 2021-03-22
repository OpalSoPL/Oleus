/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.events;

import io.github.nucleuspowered.nucleus.api.teleport.event.NucleusTeleportEvent;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public class AboutToTeleportEvent extends AbstractEvent implements NucleusTeleportEvent.AboutToTeleport {

    @Nullable private Component cancelMessage;
    private boolean isCancelled = false;

    private final Cause cause;
    private final ServerLocation location;
    private final Vector3d rotation;
    private final UUID teleportingEntity;

    public AboutToTeleportEvent(final Cause cause, final ServerLocation location, final Vector3d rotation, final UUID teleportingEntity) {
        this.cause = cause;
        this.location = location;
        this.rotation = rotation;
        this.teleportingEntity = teleportingEntity;
    }

    @Override public Optional<Component> getCancelMessage() {
        return Optional.ofNullable(this.cancelMessage);
    }

    @Override public void setCancelMessage(@Nullable final Component message) {
        this.cancelMessage = message;
    }

    @Override public ServerLocation getLocation() {
        return this.location;
    }

    @Override public Vector3d getRotation() {
        return this.rotation;
    }

    @Override public UUID getPlayer() {
        return this.teleportingEntity;
    }

    @Override public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override public Cause cause() {
        return this.cause;
    }
}
