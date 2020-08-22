/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.mute;

import io.github.nucleuspowered.nucleus.api.module.mute.data.Mute;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cause;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides methods for managing mutes within Nucleus
 */
public interface NucleusMuteService {

    /**
     * The permission context key that indicates a player is muted.
     *
     * <p>The value of this context will always be true if set.</p>
     */
    String MUTED_CONTEXT = "nucleus_muted";

    /**
     * Gets whether the player with a given {@link UUID} is muted.
     *
     * @param uuid The {@link UUID} of a user to check.
     * @return <code>true</code> if so.
     */
    boolean isMuted(UUID uuid);

    /**
     * Gets the {@link Mute} about a player.
     *
     * @param user The {@link UUID} of a user to check
     * @return The {@link Mute}, if applicable.
     */
    Optional<Mute> getPlayerMuteInfo(UUID user);

    /**
     * Mutes a player. The {@link Cause} will be used to determine the
     * source who muted the target.
     *
     * @param user The {@link UUID} of the player to mute.
     * @param reason The reason to mute them for.
     * @param duration The length of time to mute for, or <code>null</code> for indefinite.
     * @return <code>true</code> if the user was muted, <code>false</code> if the user was already muted.
     */
    boolean mutePlayer(UUID user, String reason, @Nullable Duration duration);

    /**
     * Unmutes a player.
     *
     * @param uuid The {@link UUID} of the user to unmute.
     * @return <code>true</code> if the player was unmuted.
     */
    boolean unmutePlayer(UUID uuid);
}
