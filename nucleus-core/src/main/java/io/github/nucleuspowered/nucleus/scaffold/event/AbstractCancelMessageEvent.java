/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.event;

import io.github.nucleuspowered.nucleus.api.util.CancelMessageEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractCancelMessageEvent extends AbstractEvent implements CancelMessageEvent {

    private final Cause cause;
    @Nullable private TextComponent cancelMessage = null;
    private boolean cancelled = false;

    protected AbstractCancelMessageEvent(final Cause cause) {
        this.cause = cause;
    }

    @Override public Optional<Text> getCancelMessage() {
        return Optional.ofNullable(this.cancelMessage);
    }

    @Override public void setCancelMessage(@Nullable final TextComponent message) {
        this.cancelMessage = message;
    }

    @Override public boolean isCancelled() {
        return this.cancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override public Cause getCause() {
        return this.cause;
    }
}
