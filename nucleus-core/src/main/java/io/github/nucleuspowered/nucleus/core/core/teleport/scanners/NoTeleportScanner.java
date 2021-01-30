/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.teleport.scanners;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.teleport.TeleportHelper;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.Optional;

public class NoTeleportScanner implements TeleportScanner {

    private final ResourceKey key = ResourceKey.of("nucleus", "no_scan");

    @Override
    public Optional<ServerLocation> scanFrom(
            final ServerWorld world,
            final Vector3i position,
            final int height,
            final int width,
            final int floor,
            final TeleportHelperFilter filter,
            final TeleportHelperFilter... filters) {
        final TeleportHelper teleportHelper = Sponge.getServer().getTeleportHelper();
        return teleportHelper.getSafeLocation(
                ServerLocation.of(world, position),
                height,
                width,
                floor,
                filter,
                filters
        );
    }

    @Override
    public ResourceKey getKey() {
        return this.key;
    }

}
