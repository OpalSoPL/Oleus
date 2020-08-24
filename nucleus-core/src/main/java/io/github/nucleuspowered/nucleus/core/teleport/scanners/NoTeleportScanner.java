/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.teleport.scanners;

import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Optional;

public class NoTeleportScanner implements TeleportScanner {

    @Override
    public Optional<Location<World>> scanFrom(
            final World world,
            final Vector3i position,
            final int height,
            final int width,
            final int floor,
            final TeleportHelperFilter filter,
            final TeleportHelperFilter... filters) {
        final TeleportHelper teleportHelper = Sponge.getTeleportHelper();
        return teleportHelper.getSafeLocation(
                new Location<>(world, position),
                height,
                width,
                floor,
                filter,
                filters
        );
    }

    @Override
    public String getId() {
        return "nucleus:no_scan";
    }

    @Override
    public String getName() {
        return "Nucleus No Scan";
    }

}
