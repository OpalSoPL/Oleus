/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.events;

import io.github.nucleuspowered.nucleus.api.module.message.event.NucleusHelpOpEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class InternalNucleusHelpOpEvent extends AbstractEvent implements NucleusHelpOpEvent {

    private final String message;
    private final Cause cause;
    private boolean isCancelled = false;

    public InternalNucleusHelpOpEvent(final String message) {
        this.cause = Sponge.getServer().getCauseStackManager().getCurrentCause();
        this.message = message;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
