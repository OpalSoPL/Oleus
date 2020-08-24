/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.teleport;

import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.Optional;

/**
 * Contains routines that support Nucleus safe teleports
 */
public interface NucleusSafeLocationService {

    /**
     * Find a safe location around the given location, subject to the
     * given {@link TeleportScanner} or {@link TeleportHelperFilter}.
     *
     * @param location The location to find a safe location around
     * @param scanner The {@link TeleportScanner} to use to determine how to
     *                select the next location when a safe location has not
     *                been found.
     * @param filter The first {@link TeleportHelperFilter} to use to determine
     *               whether a block is safe.
     * @param filters Additional {@link TeleportHelperFilter}s to use when
     *                when determining whether a block is safe.
     * @return The {@link Location}, if one is avaiable.
     */
    Optional<ServerLocation> getSafeLocation(
            ServerLocation location,
            TeleportScanner scanner,
            TeleportHelperFilter filter,
            TeleportHelperFilter... filters);

}
