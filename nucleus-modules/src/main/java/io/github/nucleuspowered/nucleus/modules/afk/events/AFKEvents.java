/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.events;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.api.module.afk.event.NucleusAFKEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;
import java.util.UUID;

public abstract class AFKEvents extends AbstractEvent implements NucleusAFKEvent {

    private final UUID target;
    private final Cause cause;
    private final Audience original;
    private Audience channel;
    private final Component originalMessage;
    private Component message;

    AFKEvents(final UUID target, @Nullable final Component message, @Nullable final Audience original, final Cause cause) {
        this.target = target;
        this.cause = cause;
        this.originalMessage = message == null ? Component.empty() : message;
        this.message = this.originalMessage;
        this.original = original;
        this.channel = original;
    }

    @Override
    public UUID getTargetPlayer() {
        return this.target;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Component getOriginalMessage() {
        return this.originalMessage;
    }

    @Override
    public Component getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(@Nullable final Component message) {
        if (message == null) {
            this.message = Component.empty();
        } else {
            this.message = message;
        }
    }

    @Override
    public Audience getOriginalAudience() {
        return this.original;
    }

    @Override
    public Optional<Audience> getAudience() {
        return Optional.ofNullable(this.channel);
    }

    @Override
    public void setAudience(final Audience channel) {
        this.channel = Preconditions.checkNotNull(channel);
    }

    public static class From extends AFKEvents implements NucleusAFKEvent.ReturningFromAFK {

        public From(final UUID target, @Nullable final Component message, @Nullable final Audience original, final Cause cause) {
            super(target, message, original, cause);
        }
    }

    public static class To extends AFKEvents implements NucleusAFKEvent.GoingAFK {

        public To(final UUID target, @Nullable final Component message, @Nullable final Audience original, final Cause cause) {
            super(target, message, original, cause);
        }
    }

    public static class Kick extends AFKEvents implements NucleusAFKEvent.Kick {

        private boolean cancelled = false;

        public Kick(final UUID target, final Component message, final Audience original, final Cause cause) {
            super(target, message, original, cause);
        }

        @Override public boolean isCancelled() {
            return this.cancelled;
        }

        @Override public void setCancelled(final boolean cancel) {
            this.cancelled = cancel;
        }
    }

    public static class Notify extends AFKEvents implements NucleusAFKEvent.NotifyCommand {

        @Nullable private final Component originalMessage;
        @Nullable private Component message;

        public Notify(final UUID target, @Nullable final Component message, final Audience audience, final Cause cause) {
            super(target, message, audience, cause);
            this.originalMessage = message;
            this.message = message;
        }

        @Override public Component getOriginalMessage() {
            return this.originalMessage;
        }

        @Override public Component getMessage() {
            return this.message;
        }

        @Override
        public void setMessage(@Nullable final Component message) {
            this.message = message;
        }

    }
}
