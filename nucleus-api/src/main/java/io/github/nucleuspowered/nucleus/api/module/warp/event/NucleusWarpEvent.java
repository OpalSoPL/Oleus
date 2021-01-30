/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.warp.event;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import io.github.nucleuspowered.nucleus.api.util.CancelMessageEvent;
import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Optional;
import java.util.UUID;

/**
 * Events when a server warp changes.
 */
public interface NucleusWarpEvent extends Cancellable, CancelMessageEvent, Event {

    /**
     * Get the name of the warp.
     *
     * @return The name of the warp.
     */
    String getName();

    /**
     * Fired when a warp is created.
     */
    @MightOccurAsync
    interface Create extends NucleusWarpEvent {

        /**
         * Gets the proposed {@link Location} of the warp.
         *
         * @return The location.
         */
        ServerLocation getLocation();
    }

    /**
     * Fired when a warp is deleted.
     */
    @MightOccurAsync
    interface Delete extends NucleusWarpEvent {

        /**
         * Gets the {@link Warp}
         *
         * @return The warp.
         */
        Warp getWarp();

        /**
         * Gets the {@link Location} of the warp.
         *
         * @return The location. It might not exist if the world does not exist any more.
         */
        Optional<ServerLocation> getLocation();
    }

    /**
     * Fired when a player with the given {@link UUID} tries to teleport to a warp. The
     * {@link Cause} of the event is who requests the warp, and is not necessarily the
     * {@link #getTargetUser()} who is being warped.
     *
     * <p>
     *     Note that the user does not necessarily need to be online.
     * </p>
     */
    interface Use extends NucleusWarpEvent {

        /**
         * The User being warped.
         *
         * @return The {@link User}
         */
        UUID getTargetUser();

        /**
         * Gets the {@link Warp}
         *
         * @return The warp.
         */
        Warp getWarp();

        /**
         * Gets the {@link Location} of the warp.
         *
         * @return The location.
         */
        ServerLocation getLocation();
    }
}
