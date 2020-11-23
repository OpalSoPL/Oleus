/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.teleport.scanners;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.Optional;

public abstract class VerticalTeleportScanner implements TeleportScanner {

    private final ResourceKey key;

    protected VerticalTeleportScanner(final ResourceKey key) {
        this.key = key;
    }

    @Override
    public ResourceKey getKey() {
        return this.key;
    }

    @Override
    public Optional<ServerLocation> scanFrom(
            final ServerWorld world,
            Vector3i position,
            final int width,
            final int height,
            final int floorDistance,
            final TeleportHelperFilter filter,
            final TeleportHelperFilter... filters) {
        final int maxy = world.getBlockMax().getY();
        final int jumps = (height * 2) - 1;

        do {
            final Optional<ServerLocation> result = Sponge.getServer().getTeleportHelper()
                    .getSafeLocation(
                            ServerLocation.of(world, position),
                            height,
                            width,
                            floorDistance,
                            filter,
                            filters
                    );
            if (result.isPresent()) {
                return result;
            }

            position = position.add(0, jumps, 0);
        } while (position.getY() < maxy);

        return Optional.empty();
    }

    public static class Ascending extends VerticalTeleportScanner {

        public Ascending() {
            super(ResourceKey.of("nucleus", "ascending_scan"));
        }

    }

    public static class Descending extends VerticalTeleportScanner {

        public Descending() {
            super(ResourceKey.of("nucleus", "descending_scan"));
        }

    }
}
