/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.inventory.events;

import io.github.nucleuspowered.nucleus.api.module.inventory.NucleusClearInventoryEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.UUID;

public abstract class ClearInventoryEvent extends AbstractEvent implements NucleusClearInventoryEvent {

    private final UUID target;
    private final Cause cause;
    private final boolean isClearingAll;

    public ClearInventoryEvent(final Cause cause, final UUID target, final boolean isClearingAll) {
        this.cause = cause;
        this.target = target;
        this.isClearingAll = isClearingAll;
    }

    @Override
    public UUID getUser() {
        return this.target;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

    @Override public boolean isClearingAll() {
        return this.isClearingAll;
    }

    public static class Pre extends ClearInventoryEvent implements NucleusClearInventoryEvent.Pre {

        private boolean cancelled = false;

        public Pre(final Cause cause, final UUID target, final boolean isClearingAll) {
            super(cause, target, isClearingAll);
        }

        @Override public boolean isCancelled() {
            return this.cancelled;
        }

        @Override public void setCancelled(final boolean cancel) {
            this.cancelled = cancel;
        }
    }

    public static class Post extends ClearInventoryEvent implements NucleusClearInventoryEvent.Post {

        public Post(final Cause cause, final UUID target, final boolean isClearingAll) {
            super(cause, target, isClearingAll);
        }
    }
}
