/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.mute.event;

import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Events that occur whilst muted.
 *
 * <p>
 *     These events might occur async!
 * </p>
 */
public interface NucleusMuteEvent {

    /**
     * The {@link UUID} of the muted user.
     *
     * @return The UUID
     */
    UUID getMutedUser();

    /**
     * Fired when a player is muted.
     */
    @MightOccurAsync
    interface Muted extends NucleusMuteEvent {

        /**
         * Gets the duration of the mute, if any.
         *
         * @return The duration.
         */
        Optional<Duration> getDuration();

        /**
         * The reason given for the mute.
         *
         * @return The reason.
         */
        Component getReason();
    }

    /**
     * Fired when a player is unmuted.
     */
    @MightOccurAsync
    interface Unmuted extends NucleusMuteEvent {

        /**
         * Whether the mute simply expired.
         *
         * @return <code>true</code> if so.
         */
        boolean expired();
    }
}
