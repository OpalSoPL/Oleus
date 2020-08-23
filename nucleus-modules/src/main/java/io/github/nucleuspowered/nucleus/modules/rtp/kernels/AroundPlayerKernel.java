/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.kernels;

import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;

public class AroundPlayerKernel extends DefaultKernel {

    @Override Vector3i getCentralLocation(@Nullable final Location<World> currentLocation, final World world) {
        if (currentLocation != null && world.getUniqueId().equals(currentLocation.getExtent().getUniqueId())) {
            return currentLocation.getBlockPosition();
        }

        return super.getCentralLocation(currentLocation, world);
    }

    @Override public String getId() {
        return "nucleus:around_player";
    }

    @Override public String getName() {
        return "Around Player Kernel";
    }
}
