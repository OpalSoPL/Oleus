/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.teleport.filters;

import org.spongepowered.api.data.Keys;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

abstract class FilterBase implements TeleportHelperFilter {

    @SuppressWarnings("all")
    boolean isPassable(World world, Vector3i position, boolean checkSafe) {
        BlockState block = world.getBlock(position);
        if (checkSafe && isSafeBodyMaterial(block)) {
            return false;
        }

        return block.get(Keys.IS_PASSABLE).orElse(false);
    }

}
