/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.services;

import io.github.nucleuspowered.nucleus.api.module.jail.data.Jailing;
import io.github.nucleuspowered.nucleus.modules.jail.data.JailData;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerLocation;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public abstract class JailingEntry implements Jailing {

    public static JailingEntry fromJailingData(final UUID user, final JailData jailData, final boolean isOnlineOnly) {
        if (jailData.getTimeFromNextLogin() == null) {
            return new JailingEntry.Untimed(jailData);
        }
        return Sponge.server().player(user).<JailingEntry>map(x -> new JailingEntry.Ticking(jailData))
                .orElseGet(() -> isOnlineOnly ? new JailingEntry.Stopped(jailData) : new JailingEntry.Ticking(jailData));
    }

    public static JailingEntry fromJailingRequest(final UUID user, final String reason, final String jailName,
            @Nullable final UUID jailer, @Nullable final ServerLocation previousLocation,
            @Nullable final Instant creationInstant, @Nullable final Duration timeRemaining) {
        if (timeRemaining == null) {
            return new JailingEntry.Untimed(reason, jailName, jailer, previousLocation, creationInstant);
        }

        if (Sponge.server().player(user).isPresent()) {
            return new JailingEntry.Ticking(reason, jailName, jailer, previousLocation, creationInstant, Instant.now().plus(timeRemaining));
        }
        return new JailingEntry.Stopped(reason, jailName, jailer, previousLocation, creationInstant, timeRemaining);
    }

    @Nullable
    private static ServerLocation getLocation(final JailData jailData) {
        if (jailData.getPreviousy() == -1) {
            return null;
        }
        return ServerLocation.of(jailData.worldKey(), jailData.getPreviousx(), jailData.getPreviousy(), jailData.getPreviousz());
    }

    final String reason;
    final String jailName;
    @Nullable final UUID jailer;
    @Nullable final ServerLocation previousLocation;
    @Nullable final Instant creationInstant;

    protected JailingEntry(final JailData jailData) {
        this(jailData.getReason(), jailData.getJailName(), jailData.getJailer(), JailingEntry.getLocation(jailData),
                Instant.ofEpochSecond(jailData.getCreationTime()));
    }

    protected JailingEntry(final String reason, final String jailName, @Nullable final UUID jailer,
            @Nullable final ServerLocation previousLocation, @Nullable final Instant creationInstant) {
        this.reason = reason;
        this.jailName = jailName;
        this.jailer = jailer;
        this.previousLocation = previousLocation;
        this.creationInstant = creationInstant;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getJailName() {
        return this.jailName;
    }

    @Override
    public Optional<UUID> getJailer() {
        return Optional.ofNullable(this.jailer);
    }

    @Override
    public Optional<ServerLocation> getPreviousLocation() {
        return Optional.ofNullable(this.previousLocation);
    }

    @Override
    public Optional<Instant> getCreationInstant() {
        return Optional.ofNullable(this.creationInstant);
    }

    @Override
    public Optional<Duration> getRemainingTime() {
        return Optional.empty();
    }

    public abstract JailData asJailData(final boolean tickOnlineOnly);

    public final static class Untimed extends JailingEntry {

        Untimed(final JailData jailData) {
            super(jailData);
        }

        public Untimed(final String reason, final String jailName, @Nullable final UUID jailer,
                @Nullable final ServerLocation previousLocation, @Nullable final Instant creationInstant) {
            super(reason, jailName, jailer, previousLocation, creationInstant);
        }

        @Override
        public Optional<Duration> getRemainingTime() {
            return Optional.empty();
        }

        @Override
        public boolean expired() {
            return false;
        }

        @Override
        public boolean isCurrentlyTicking() {
            return false;
        }

        @Override
        public JailData asJailData(final boolean tickOnlineOnly) {
            return new JailData(
                    this.jailer,
                    this.jailName,
                    this.reason,
                    this.creationInstant,
                    this.previousLocation,
                    null,
                    null
            );
        }
    }

    public final static class Ticking extends JailingEntry {

        private final Instant endTime;

        Ticking(final JailData jailData) {
            super(jailData);
            this.endTime = Instant.now().plus(jailData.getTimeFromNextLogin(), ChronoUnit.SECONDS);
        }

        public Ticking(final String reason, final String jailName, @Nullable final UUID jailer, @Nullable final ServerLocation previousLocation,
                @Nullable final Instant creationInstant, final Instant endTime) {
            super(reason, jailName, jailer, previousLocation, creationInstant);
            this.endTime = endTime;
        }

        @Override
        public boolean expired() {
            return this.endTime.isBefore(Instant.now());
        }

        @Override
        public boolean isCurrentlyTicking() {
            return true;
        }

        @Override
        public JailData asJailData(final boolean tickOnlineOnly) {
            final Duration d = this.expired() ? Duration.ZERO : Duration.between(Instant.now(), this.endTime);
            return new JailData(
                    this.jailer,
                    this.jailName,
                    this.reason,
                    this.creationInstant,
                    this.previousLocation,
                    tickOnlineOnly ? null : Instant.now().plus(d),
                    tickOnlineOnly ? d : null
            );
        }

    }

    public final static class Stopped extends JailingEntry {

        private final Duration timeRemaining;

        Stopped(final JailData jailData) {
            super(jailData);
            this.timeRemaining = Duration.ofSeconds(jailData.getTimeFromNextLogin());
        }

        public Stopped(final String reason, final String jailName, @Nullable final UUID jailer, @Nullable final ServerLocation previousLocation,
                @Nullable final Instant creationInstant, final Duration timeRemaining) {
            super(reason, jailName, jailer, previousLocation, creationInstant);
            this.timeRemaining = timeRemaining;
        }

        @Override
        public Optional<Duration> getRemainingTime() {
            return Optional.of(this.timeRemaining);
        }

        @Override
        public boolean expired() {
            return false;
        }

        @Override
        public boolean isCurrentlyTicking() {
            return false;
        }

        @Override
        public JailData asJailData(final boolean tickOnlineOnly) {
            return new JailData(
                    this.jailer,
                    this.jailName,
                    this.reason,
                    this.creationInstant,
                    this.previousLocation,
                    null,
                    this.timeRemaining
            );
        }
    }

}
