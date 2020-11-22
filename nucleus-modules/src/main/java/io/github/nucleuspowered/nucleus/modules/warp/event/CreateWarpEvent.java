/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.event;

import io.github.nucleuspowered.nucleus.api.module.warp.event.NucleusWarpEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.ServerLocation;

public class CreateWarpEvent extends AbstractWarpEvent implements NucleusWarpEvent.Create {

    private final ServerLocation location;

    public CreateWarpEvent(final Cause cause, final String name, final ServerLocation location) {
        super(cause, name);
        this.location = location;
    }

    @Override public ServerLocation getLocation() {
        return this.location;
    }
}
