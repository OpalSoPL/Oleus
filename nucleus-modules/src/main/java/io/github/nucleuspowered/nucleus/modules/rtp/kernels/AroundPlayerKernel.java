/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.kernels;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

public class AroundPlayerKernel extends DefaultKernel {

    private static final ResourceKey AROUND_PLAYER_KERNEL_KEY = ResourceKey.of("nucleus", "around_player");

    @Override
    public ResourceKey getKey() {
        return AroundPlayerKernel.AROUND_PLAYER_KERNEL_KEY;
    }

    @Override Vector3i getCentralLocation(@Nullable final ServerLocation currentLocation, final ServerWorld world) {
        if (currentLocation != null && world.key().equals(currentLocation.worldKey())) {
            return currentLocation.blockPosition();
        }

        return super.getCentralLocation(currentLocation, world);
    }

}
