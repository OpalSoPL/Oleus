/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.inventory;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.UUID;

/**
 * Fired when a player's inventory is cleared. Should be used to clear other, secondary, inventories
 */
public interface NucleusClearInventoryEvent extends Event {

    /**
     * Gets the {@link UUID} of the user being cleared.
     *
     * @return The UUID of the user.
     */
    UUID getUser();

    /**
     * Gets whether the entire inventory is being cleared, or the
     * main player inventory only.
     *
     * @return whether the entire inventory is being cleared or not
     */
    boolean isClearingAll();

    /**
     * Called before any clearing takes effect. May be cancelled. Should not be used to clear
     * inventories (use {@link Post} for that).
     */
    interface Pre extends NucleusClearInventoryEvent, Cancellable { }

    /**
     * Called when standard inventories have been cleared and should be used to clear other
     * inventories.
     */
    interface Post extends NucleusClearInventoryEvent { }

}
