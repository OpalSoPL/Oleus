/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.teleport.data;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.function.Supplier;

public final class NucleusTeleportHelperFilters {

    /**
     * Returns the location that is passed into the filter.
     */
    public final static Supplier<TeleportHelperFilter> NO_CHECK =
            () -> Sponge.getRegistry().getCatalogRegistry().get(TeleportHelperFilter.class, ResourceKey.of("nucleus", "no_check")).get();

    /**
     * Returns a location that is not a wall.
     */
    public final static Supplier<TeleportHelperFilter> WALL_CHECK =
            () -> Sponge.getRegistry().getCatalogRegistry().get(TeleportHelperFilter.class, ResourceKey.of("nucleus", "wall_check")).get();

}
