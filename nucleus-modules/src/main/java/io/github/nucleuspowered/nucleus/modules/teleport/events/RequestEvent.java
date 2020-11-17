/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.events;

import io.github.nucleuspowered.nucleus.api.teleport.event.NucleusTeleportEvent;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;

public abstract class RequestEvent extends AbstractEvent implements NucleusTeleportEvent.Request {

    @Nullable private Component cancelMessage;
    private boolean isCancelled = false;

    private final Cause cause;
    private final ServerPlayer targetEntity;

    private RequestEvent(final Cause cause, final ServerPlayer targetEntity) {
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
    public ServerPlayer getPlayer() {
        return this.targetEntity;
    }

    @Override public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override public Cause getCause() {
        return this.cause;
    }

    public static class CauseToPlayer extends RequestEvent implements NucleusTeleportEvent.Request.CauseToPlayer {

        public CauseToPlayer(final Cause cause, final ServerPlayer targetEntity) {
            super(cause, targetEntity);
        }
    }

    public static class PlayerToCause extends RequestEvent implements NucleusTeleportEvent.Request.PlayerToCause {

        public PlayerToCause(final Cause cause, final ServerPlayer targetEntity) {
            super(cause, targetEntity);
        }
    }
}
