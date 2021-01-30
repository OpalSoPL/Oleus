/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.teleport.data;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.function.Supplier;

public final class NucleusTeleportHelperFilters {

    /**
     * Returns the location that is passed into the filter.
     */
    public final static DefaultedRegistryReference<TeleportHelperFilter> NO_CHECK =
            RegistryTypes.TELEPORT_HELPER_FILTER.defaultReferenced(ResourceKey.of("nucleus", "no_check"));

    /**
     * Returns a location that is not a wall.
     */
    public final static DefaultedRegistryReference<TeleportHelperFilter> WALL_CHECK =
            RegistryTypes.TELEPORT_HELPER_FILTER.defaultReferenced(ResourceKey.of("nucleus", "wall_check"));

}
