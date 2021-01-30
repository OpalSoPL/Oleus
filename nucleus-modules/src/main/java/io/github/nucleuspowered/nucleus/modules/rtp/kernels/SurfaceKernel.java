/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.kernels;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;
import org.spongepowered.api.world.teleport.TeleportHelperFilters;

public class SurfaceKernel extends DefaultKernel {

    private static final ResourceKey SURFACE_KERNEL_KEY = ResourceKey.of("nucleus", "surface_only");

    @Override
    public ResourceKey getKey() {
        return SurfaceKernel.SURFACE_KERNEL_KEY;
    }

    @Nullable
    @Override
    ServerLocation getStartingLocation(final ServerLocation location) {
        return super.getStartingLocation(
                ServerLocation.of(location.getWorld(), location.getBlockX(), location.getWorld().getHighestYAt(location.getBlockX(), location.getBlockZ()),
                        location.getBlockZ()));
    }

    @Override
    TeleportHelperFilter filterToUse() {
        return TeleportHelperFilters.SURFACE_ONLY.get();
    }

}
