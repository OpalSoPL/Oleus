/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.services;

import io.github.nucleuspowered.nucleus.api.module.mute.data.Mute;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public abstract class MutedEntry implements Mute {

    public static MutedEntry fromMuteData(final UUID user, final MuteData muteData, final boolean isOnlineOnly) {
        if (muteData.getTimeFromNextLogin() == null) {
            return new MutedEntry.Untimed(muteData);
        }
        return Sponge.getServer().getPlayer(user).<MutedEntry>map(x -> new MutedEntry.Ticking(muteData))
                .orElseGet(() -> isOnlineOnly ? new MutedEntry.Stopped(muteData) : new MutedEntry.Ticking(muteData));
    }

    public static MutedEntry fromMutingRequest(final UUID user, final String reason,
            @Nullable final UUID jailer,
            @Nullable final Instant creationInstant, @Nullable final Duration timeRemaining) {
        if (timeRemaining == null) {
            return new MutedEntry.Untimed(reason, jailer, creationInstant);
        }

        if (Sponge.getServer().getPlayer(user).isPresent()) {
            return new MutedEntry.Ticking(reason, jailer, creationInstant, Instant.now().plus(timeRemaining));
        }
        return new MutedEntry.Stopped(reason, jailer, creationInstant, timeRemaining);
    }

    final String reason;
    @Nullable final UUID muter;
    @Nullable final Instant creationInstant;

    protected MutedEntry(final MuteData jailData) {
        this(jailData.getReason(), jailData.getMuter(), Instant.ofEpochSecond(jailData.getCreationTime()));
    }

    protected MutedEntry(final String reason, @Nullable final UUID muter, @Nullable final Instant creationInstant) {
        this.reason = reason;
        this.muter = muter;
        this.creationInstant = creationInstant;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public Optional<UUID> getMuter() {
        return Optional.ofNullable(this.muter);
    }

    @Override
    public Optional<Instant> getCreationInstant() {
        return Optional.ofNullable(this.creationInstant);
    }

    @Override
    public Optional<Duration> getRemainingTime() {
        return Optional.empty();
    }

    public abstract MuteData asMuteData(final boolean tickOnlineOnly);

    public final static class Untimed extends MutedEntry {

        Untimed(final MuteData muteData) {
            super(muteData);
        }

        public Untimed(final String reason, @Nullable final UUID jailer, @Nullable final Instant creationInstant) {
            super(reason, jailer, creationInstant);
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
        public MuteData asMuteData(final boolean tickOnlineOnly) {
            return new MuteData(
                    this.muter,
                    this.reason,
                    this.creationInstant,
                    null,
                    null
            );
        }
    }

    public final static class Ticking extends MutedEntry {

        private final Instant endTime;

        Ticking(final MuteData jailData) {
            super(jailData);
            this.endTime = Instant.now().plus(jailData.getTimeFromNextLogin(), ChronoUnit.SECONDS);
        }

        public Ticking(final String reason, @Nullable final UUID jailer, @Nullable final Instant creationInstant, final Instant endTime) {
            super(reason, jailer, creationInstant);
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
        public MuteData asMuteData(final boolean tickOnlineOnly) {
            final Duration d = this.expired() ? Duration.ZERO : Duration.between(Instant.now(), this.endTime);
            return new MuteData(
                    this.muter,
                    this.reason,
                    this.creationInstant,
                    tickOnlineOnly ? d : null,
                    tickOnlineOnly ? null : Instant.now().plus(d)
            );
        }

    }

    public final static class Stopped extends MutedEntry {

        private final Duration timeRemaining;

        Stopped(final MuteData jailData) {
            super(jailData);
            this.timeRemaining = Duration.ofSeconds(jailData.getTimeFromNextLogin());
        }

        public Stopped(final String reason, @Nullable final UUID jailer, @Nullable final Instant creationInstant, final Duration timeRemaining) {
            super(reason, jailer, creationInstant);
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
        public MuteData asMuteData(final boolean tickOnlineOnly) {
            return new MuteData(
                    this.muter,
                    this.reason,
                    this.creationInstant,
                    this.timeRemaining,
                    null
            );
        }
    }

}
