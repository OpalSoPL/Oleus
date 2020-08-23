/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.events;

import io.github.nucleuspowered.nucleus.api.module.mute.event.NucleusMuteEvent;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;
import java.time.Duration;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class MuteEvent extends AbstractEvent implements NucleusMuteEvent {

    private final Cause cause;
    private final User target;

    public MuteEvent(final Cause cause, final User target) {
        this.cause = cause;
        this.target = target;
    }

    @Override public User getTargetUser() {
        return this.target;
    }

    @Override public Cause getCause() {
        return this.cause;
    }

    public static class Muted extends MuteEvent implements NucleusMuteEvent.Muted {

        @Nullable public final Duration duration;
        public final TextComponent reason;

        public Muted(final Cause cause, final User target, @Nullable final Duration duration, final TextComponent reason) {
            super(cause, target);
            this.duration = duration;
            this.reason = reason;
        }

        @Override public Optional<Duration> getDuration() {
            return Optional.ofNullable(this.duration);
        }

        @Override public TextComponent getReason() {
            return this.reason;
        }
    }

    public static class Unmuted extends MuteEvent implements NucleusMuteEvent.Unmuted {

        private final boolean expired;

        public Unmuted(final Cause cause, final User target, final boolean expired) {
            super(cause, target);
            this.expired = expired;
        }

        @Override public boolean expired() {
            return this.expired;
        }
    }
}
