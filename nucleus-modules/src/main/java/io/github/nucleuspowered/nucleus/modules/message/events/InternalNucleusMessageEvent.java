/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.events;

import io.github.nucleuspowered.nucleus.api.module.message.event.NucleusMessageEvent;
import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class InternalNucleusMessageEvent extends AbstractEvent implements NucleusMessageEvent {

    private final Cause cause;
    private final MessageTarget from;
    private final MessageTarget to;
    private final String message;
    private boolean isCancelled = false;

    public InternalNucleusMessageEvent(final Cause cause, final MessageTarget from, final MessageTarget to, final String message) {
        this.cause = cause;
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public MessageTarget getSender() {
        return this.from;
    }

    @Override
    public MessageTarget getReceiver() {
        return this.to;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }
}
