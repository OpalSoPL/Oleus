/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.events;

import io.github.nucleuspowered.nucleus.api.module.mail.event.NucleusSendMailEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;
import java.util.UUID;

public final class InternalNucleusSendMailEvent extends AbstractEvent implements NucleusSendMailEvent {

    @Nullable private final UUID from;
    private final UUID to;
    private final String message;
    private final Cause cause;
    private boolean cancelled = false;

    public InternalNucleusSendMailEvent(@Nullable final UUID from, final UUID to, final String message) {
        this.cause = Sponge.server().causeStackManager().currentCause();
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Optional<UUID> getSender() {
        return Optional.ofNullable(this.from);
    }

    @Override
    public UUID getRecipient() {
        return this.to;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
