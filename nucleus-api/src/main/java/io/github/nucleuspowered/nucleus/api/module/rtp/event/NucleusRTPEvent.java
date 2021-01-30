/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.rtp.event;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Events for RTP
 */
public interface NucleusRTPEvent {

    /**
     * The {@link ServerPlayer} currently being teleported.
     *
     * @return The {@link ServerPlayer}
     */
    ServerPlayer getTargetPlayer();

    /**
     * Fired when the RTP system has selected a location
     *
     * <p>Cancelling this event will cause the RTP system
     * to look for another location</p>
     */
    interface SelectedLocation extends NucleusRTPEvent, Cancellable {

        /**
         * Gets the proposed location
         *
         * @return The location
         */
        ServerLocation getLocation();

    }
}
