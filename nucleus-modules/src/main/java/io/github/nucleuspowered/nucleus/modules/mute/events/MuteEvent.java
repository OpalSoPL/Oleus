/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.events;

import io.github.nucleuspowered.nucleus.api.module.mute.event.NucleusMuteEvent;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public abstract class MuteEvent extends AbstractEvent implements NucleusMuteEvent {

    private final Cause cause;
    private final UUID mutedUser;

    public MuteEvent(final Cause cause, final UUID mutedUser) {
        this.cause = cause;
        this.mutedUser = mutedUser;
    }

    @Override
    public UUID getMutedUser() {
        return this.mutedUser;
    }

    @Override public Cause getCause() {
        return this.cause;
    }

    public static class Muted extends MuteEvent implements NucleusMuteEvent.Muted {

        @Nullable public final Duration duration;
        public final Component reason;

        public Muted(final Cause cause, final UUID target, @Nullable final Duration duration, final Component reason) {
            super(cause, target);
            this.duration = duration;
            this.reason = reason;
        }

        @Override public Optional<Duration> getDuration() {
            return Optional.ofNullable(this.duration);
        }

        @Override public Component getReason() {
            return this.reason;
        }
    }

    public static class Unmuted extends MuteEvent implements NucleusMuteEvent.Unmuted {

        private final boolean expired;

        public Unmuted(final Cause cause, final UUID target, final boolean expired) {
            super(cause, target);
            this.expired = expired;
        }

        @Override public boolean expired() {
            return this.expired;
        }
    }
}
