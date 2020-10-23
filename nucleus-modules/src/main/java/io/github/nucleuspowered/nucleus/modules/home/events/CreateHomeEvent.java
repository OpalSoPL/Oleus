/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.events;

import io.github.nucleuspowered.nucleus.api.module.home.event.NucleusHomeEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.ServerLocation;

import java.util.UUID;

public class CreateHomeEvent extends AbstractHomeEvent implements NucleusHomeEvent.Create {

    public CreateHomeEvent(final String name, final UUID owner, final Cause cause, final ServerLocation newLocation) {
        super(name, owner, cause, newLocation);
    }
}
