/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.teleport;

import org.spongepowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.Optional;

/**
 * Contains routines that support Nucleus safe teleports
 */
public interface NucleusSafeTeleportService {

    /**
     * Teleports a player to a specified location, using a
     * {@link TeleportScanner} that determines how to select a location.
     *
     * @param player The player to teleport
     * @param location The approximate location to teleport to
     * @param centreBlock If true, teleport a player to the centre of a block
     *                    else, teleports to a conrner.
     * @param scanner The {@link TeleportScanner} to use to determine how to
     *                select the next location when a safe location has not
     *                been found.
     * @param filter The first {@link TeleportHelperFilter} to use to determine
     *               whether a block is safe.
     * @param filters Additional {@link TeleportHelperFilter}s to use when
     *                when determining whether a block is safe.
     * @return The result of the teleport.
     */
    TeleportResult teleportPlayer(Player player,
            Location<World> location,
            boolean centreBlock,
            TeleportScanner scanner,
            TeleportHelperFilter filter,
            TeleportHelperFilter... filters);

    /**
     * Teleports a player to a specified location, using a
     * {@link TeleportScanner} that determines how to select a location.
     *
     * @param player The player to teleport
     * @param location The approximate location to teleport to
     * @param rotation The rotation of the player upon teleport
     * @param centreBlock If true, teleport a player to the centre of a block
     *                    else, teleports to a conrner.
     * @param scanner The {@link TeleportScanner} to use to determine how to
     *                select the next location when a safe location has not
     *                been found.
     * @param filter The first {@link TeleportHelperFilter} to use to determine
     *               whether a block is safe.
     * @param filters Additional {@link TeleportHelperFilter}s to use when
     *                when determining whether a block is safe.
     * @return The result of the teleport.
     */
    TeleportResult teleportPlayer(Player player,
            Location<World> location,
            Vector3d rotation,
            boolean centreBlock,
            TeleportScanner scanner,
            TeleportHelperFilter filter,
            TeleportHelperFilter... filters);

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
    Optional<Location<World>> getSafeLocation(
            Location<World> location,
            TeleportScanner scanner,
            TeleportHelperFilter filter,
            TeleportHelperFilter... filters);

    /**
     * Find a safe location around the given location, subject to the
     * given {@link TeleportScanner} or {@link TeleportHelperFilter}.
     *
     * @param location The location to find a safe location around
     * @param rotation The rotation for the {@link Transform}
     * @param scanner The {@link TeleportScanner} to use to determine how to
     *                select the next location when a safe location has not
     *                been found.
     * @param filter The first {@link TeleportHelperFilter} to use to determine
     *               whether a block is safe.
     * @param filters Additional {@link TeleportHelperFilter}s to use when
     *                when determining whether a block is safe.
     * @return The {@link Location}, if one is avaiable.
     */
    Optional<Transform<World>> getSafeTransform(
            Location<World> location,
            Vector3d rotation,
            TeleportScanner scanner,
            TeleportHelperFilter filter,
            TeleportHelperFilter... filters);
}
