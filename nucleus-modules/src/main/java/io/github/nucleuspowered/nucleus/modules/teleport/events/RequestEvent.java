/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.events;

import io.github.nucleuspowered.nucleus.api.teleport.event.NucleusTeleportEvent;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;
import java.util.UUID;

public abstract class RequestEvent extends AbstractEvent implements NucleusTeleportEvent.Request {

    @Nullable private Component cancelMessage;
    private boolean isCancelled = false;

    private final Cause cause;
    private final UUID targetEntity;

    private RequestEvent(final Cause cause, final UUID targetEntity) {
        this.cause = cause;
        this.targetEntity = targetEntity;
    }

    @Override public Optional<Component> getCancelMessage() {
        return Optional.ofNullable(this.cancelMessage);
    }

    @Override public void setCancelMessage(@Nullable final Component message) {
        this.cancelMessage = message;
    }

    @Override
    public UUID getPlayer() {
        return this.targetEntity;
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

    public static class CauseToPlayer extends RequestEvent implements NucleusTeleportEvent.Request.CauseToPlayer {

        public CauseToPlayer(final Cause cause, final UUID targetEntity) {
            super(cause, targetEntity);
        }
    }

    public static class PlayerToCause extends RequestEvent implements NucleusTeleportEvent.Request.PlayerToCause {

        public PlayerToCause(final Cause cause, final UUID targetEntity) {
            super(cause, targetEntity);
        }
    }
}
