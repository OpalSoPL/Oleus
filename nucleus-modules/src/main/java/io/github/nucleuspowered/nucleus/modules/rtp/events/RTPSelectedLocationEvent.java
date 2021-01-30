/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.events;

import io.github.nucleuspowered.nucleus.api.module.rtp.event.NucleusRTPEvent;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.server.ServerLocation;

public class RTPSelectedLocationEvent extends AbstractEvent implements NucleusRTPEvent.SelectedLocation {

    private final ServerLocation location;
    private final ServerPlayer player;
    private final Cause cause;
    private boolean isCancelled = false;

    public RTPSelectedLocationEvent(final ServerLocation location, final ServerPlayer player, final Cause cause) {
        this.location = location;
        this.player = player;
        this.cause = cause;
    }

    @Override
    public ServerLocation getLocation() {
        return this.location;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public ServerPlayer getTargetPlayer() {
        return this.player;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }
}
