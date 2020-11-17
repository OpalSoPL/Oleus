/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.events;

import io.github.nucleuspowered.nucleus.api.module.jail.event.NucleusJailEvent;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public abstract class JailEvent extends AbstractEvent implements NucleusJailEvent {

    private final UUID targetUser;
    private final Cause cause;

    private JailEvent(final UUID targetUser, final Cause cause) {
        this.targetUser = targetUser;
        this.cause = cause;
    }

    @Override
    public UUID getJailedUser() {
        return this.targetUser;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public static class Jailed extends JailEvent implements NucleusJailEvent.Jailed {

        private final String jailName;
        private final Component reason;
        @Nullable private final Duration duration;

        public Jailed(final UUID targetUser, final Cause cause, final String jailName, final Component reason, @Nullable final Duration duration) {
            super(targetUser, cause);
            this.jailName = jailName;
            this.reason = reason;
            this.duration = duration;
        }

        @Override public String getJailName() {
            return this.jailName;
        }

        @Override public Optional<Duration> getDuration() {
            return Optional.ofNullable(this.duration);
        }

        @Override public Component getReason() {
            return this.reason;
        }
    }

    public static class Unjailed extends JailEvent implements NucleusJailEvent.Unjailed {

        public Unjailed(final UUID targetUser, final Cause cause) {
            super(targetUser, cause);
        }
    }
}
