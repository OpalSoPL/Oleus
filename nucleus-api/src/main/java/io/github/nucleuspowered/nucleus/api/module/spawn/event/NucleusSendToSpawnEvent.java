/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.spawn.event;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.ServerLocation;

/**
 * Called when Nucleus has been requested to send a {@link User} to spawn, either now, or on their next login.
 */
public interface NucleusSendToSpawnEvent extends Cancellable {

    /**
     * The {@link ServerLocation} to send the {@link User} to.
     *
     * @return The {@link ServerLocation}
     */
    ServerLocation getTargetLocation();

    /**
     * The original {@link ServerLocation} Nucleus was going to send the user to.
     *
     * @return The {@link ServerLocation}
     */
    ServerLocation getOriginalTargetLocation();

    /**
     * The {@link ServerLocation} to redirect the user to.
     *
     * @param transform The {@link ServerLocation}
     */
    void setTransformTo(ServerLocation transform);

    /**
     * If cancelled, the reason to return to the requestee.
     *
     * @param reason The reason for cancelling.
     */
    void setCancelReason(String reason);

    /**
     * The {@link User} being spawned.
     *
     * @return The {@link User}
     */
    User getTargetUser();

    /**
     * The type of the spawn event.
     */
    enum Type {COMMAND, DEATH, HOME_ON_DEATH, PLUGIN}
}
