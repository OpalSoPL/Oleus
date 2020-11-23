/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.kernels;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.rtp.NucleusRTPService;
import io.github.nucleuspowered.nucleus.api.module.rtp.kernel.RTPKernel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.teleport.TeleportHelper;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;
import org.spongepowered.api.world.teleport.TeleportHelperFilters;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;

public class DefaultKernel implements RTPKernel {

    public static final DefaultKernel INSTANCE = new DefaultKernel();

    private static final ResourceKey DEFAULT_KERNEL_KEY = ResourceKey.of("nucleus", "default");

    @Override
    public ResourceKey getKey() {
        return DefaultKernel.DEFAULT_KERNEL_KEY;
    }

    @Override
    public Optional<ServerLocation> getLocation(@Nullable final ServerLocation currentLocation, final ServerWorld target, final NucleusRTPService.RTPOptions options) {
        // from world spawn
        Vector3d location;
        int count = 25;
        do {
            if (--count < 0) {
                // We found nothing in the timeframe.
                return Optional.empty();
            }

            location = KernelHelper.INSTANCE.getLocationWithOffset(this.getCentralLocation(currentLocation, target), options);
        } while (!Util.isLocationInWorldBorder(location.toDouble(), target));

        final ServerLocation worldLocation = this.getStartingLocation(ServerLocation.of(target, location));
        if (worldLocation == null) {
            return Optional.empty();
        }

        final Optional<ServerLocation> targetLocation = Sponge.getServer().getTeleportHelper().getSafeLocation(worldLocation,
                TeleportHelper.DEFAULT_HEIGHT,
                TeleportHelper.DEFAULT_WIDTH,
                TeleportHelper.DEFAULT_FLOOR_CHECK_DISTANCE,
                TeleportHelperFilters.CONFIG.get(),
                this.filterToUse());
        if (targetLocation.isPresent()) {
            // Is it in the world border?
            if (!Util.isLocationInWorldBorder(worldLocation)
                    || options.prohibitedBiomes().contains(worldLocation.getBiome())
                    || options.minHeight() > worldLocation.getBlockY()
                    || options.maxHeight() < worldLocation.getBlockY()) {
                return Optional.empty();
            }

            return this.verifyLocation(targetLocation.get()) ? targetLocation : Optional.empty();
        }

        return Optional.empty();
    }

    TeleportHelperFilter filterToUse() {
        return TeleportHelperFilters.DEFAULT.get();
    }

    Vector3i getCentralLocation(@Nullable final ServerLocation currentLocation, final ServerWorld world) {
        return world.getProperties().getSpawnPosition();
    }

    @Nullable ServerLocation getStartingLocation(ServerLocation world) {
        while (world.getBlockType() == BlockTypes.AIR.get()) {
            if (world.getY() < 1) {
                return null;
            }
            world = world.sub(0, 1, 0);
        }

        return world;
    }

    boolean verifyLocation(final ServerLocation location) {
        return true;
    }

}
