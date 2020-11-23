/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.teleport.filters;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;
import org.spongepowered.math.vector.Vector3i;

public final class NoCheckFilter implements TeleportHelperFilter {

    private final ResourceKey key = ResourceKey.resolve("nucleus:no_check");

    @Override
    public Tristate isValidLocation(final ServerWorld world, final Vector3i position) {
        return Tristate.TRUE;
    }

    @Override
    public boolean isSafeFloorMaterial(final BlockState blockState) {
        return true;
    }

    @Override
    public boolean isSafeBodyMaterial(final BlockState blockState) {
        return true;
    }

    @Override
    public ResourceKey getKey() {
        return this.key;
    }
}
