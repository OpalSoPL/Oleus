/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.teleport.filters;

import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

public class WallCheckFilter extends FilterBase {

    @Override
    public Tristate isValidLocation(final World world, final Vector3i position) {
        // Check that the block is not solid.
        if (this.isPassable(world, position, false) && this.isPassable(world, position.add(0, 1, 0), false)) {
            return Tristate.TRUE;
        }

        return Tristate.FALSE;
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
    public String getId() {
        return "nucleus:wall_check";
    }

    @Override
    public String getName() {
        return "Nucleus Wall Check filter";
    }

}
