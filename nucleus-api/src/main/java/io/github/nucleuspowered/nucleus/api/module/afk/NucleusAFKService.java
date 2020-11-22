/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.afk;

import io.github.nucleuspowered.nucleus.api.util.NoExceptionAutoClosable;
import net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Allows plugins to see a player's AFK status.
 */
public interface NucleusAFKService {

    /**
     * Returns whether the user with the specified {@link UUID} can go AFK.
     *
     * @param user The {@link UUID} of the user in question
     * @return Whether or not the player can go AFK.
     */
    boolean canGoAFK(UUID user);

    /**
     * Returns a collection of {@link UUID}s of players who are currently AFK.
     *
     * @return A {@link Collection} of {@link UUID}s representing players that
     *      are currently AFK.
     */
    Collection<UUID> getAfk();

    /**
     * Returns whether a player is AFK
     *
     * @param player The {@link UUID} of the player in question.
     * @return Whether the player is AFK.
     */
    boolean isAFK(UUID player);

    /**
     * Sets a player's AFK status, if the player can go AFK.
     *
     * @param player The {@link UUID} of the player to set the status of.
     * @param isAfk Whether the player should go AFK.
     *
     * @return <code>true</code> if successful, otherwise <code>false</code>, usually because the player is exempt from going AFK.
     */
    boolean setAFK(UUID player, boolean isAfk);

    /**
     * Returns whether a user can be kicked for inactivity.
     *
     * @param user The {@link UUID} of the user in question.
     * @return Whether the player can be kicked.
     */
    boolean canBeKicked(UUID user);

    /**
     * Returns the last recorded active moment of the player.
     *
     * @param player The {@link UUID} of the player in question
     * @return The {@link Instant}
     */
    Instant lastActivity(UUID player);

    /**
     * Returns the {@link Duration} since last recorded active moment of the
     * player.
     *
     * @param player The {@link UUID} of the player in question
     * @return The {@link Instant}
     */
    default Duration timeSinceLastActivity(final UUID player) {
        return Duration.between(this.lastActivity(player), Instant.now());
    }

    /**
     * Returns how long the specified user with the given {@link UUID} has to
     * be inactive before going AFK.
     *
     * @param user The {@link UUID} in question.
     * @return The {@link Duration}, or {@link Optional#empty()} if the player
     *      cannot go AFK.
     */
    Optional<Duration> timeForInactivity(UUID user);

    /**
     * Returns how long the specified {@link UUID} has to be inactive before
     * being kicked.
     *
     * @param user The {@link UUID} in question.
     * @return The {@link Duration}, or {@link Optional#empty()} if the player cannot go AFK.
     */
    Optional<Duration> timeForKick(UUID user);

    /**
     * Checks if the {@code potentialAfkUser} is AFK and, if so,
     * attempts to notify the {@code userToNotify}.
     *
     * @param audience The {@link Audience} to notify
     * @param potentialAfkUser The {@link UUID} of the player that might be AFK.
     * @return true if a notification was sent
     */
    AFKNotificationResult notifyIsAfk(Audience audience, UUID potentialAfkUser);

    /**
     * Invalidates cached permissions, used to resync a player's exemption status.
     */
    void invalidateCachedPermissions();

    /**
     * Forces an activity tracking update for a player with given {@link UUID},
     * such that Nucleus thinks that the player has recently been active and
     * resets their AFK timer.
     *
     * @param player The player to update the activity of.
     */
    void updateActivityForUser(UUID player);

    /**
     * Disables activity tracking for the specified player for the next
     * tick. See {@link #disableTrackingForPlayer(UUID, Duration)} for more
     * information on how to use this method.
     *
     * @param player Player to disable tracking for.
     * @return The {@link AutoCloseable} that will re-enable the tracking when
     * done.
     *
     * @see #disableTrackingForPlayer(UUID, Duration)
     */
    default NoExceptionAutoClosable disableTrackingForPlayer(final UUID player) {
        return this.disableTrackingForPlayer(player, Duration.ZERO);
    }

    /**
     * Disables activity tracking for the player with the specified {@link UUID}
     * for up to the number of ticks specified.
     *
     * <p>
     *     This method returns an {@link AutoCloseable}, and as such, the
     *     recommended way of using this method is using "try with resources":
     * </p>
     * <pre>
     *     try (AutoClosable dummy = disableTrackingFor(player, 1)){
     *         // perform actions here, most likely something like:
     *         player.setLocation(location);
     *
     *         // Any actions here will not disable the AFK timer.
     *     }
     *
     *     // any actions here that move the player will be tracked again.
     * </pre>
     * <p>
     *     This pattern isn't strictly required, as the {@link AutoCloseable}
     *     will close itself after the specified number of ticks. However, it's
     *     prudent to consider the following:
     * </p>
     * <ul>
     *     <li>
     *         This method will reset the tracking on the main thread. This
     *         means if you use this async (though usually, you wouldn't do so),
     *         you will possibly find that the tracking will re-enable before
     *         the task finishes on the defaults. Minimise the amount of time
     *         the activity tracking must be disabled, and consider increasing
     *         the tick count slightly.
     *     </li>
     *     <li>
     *         There is no need to increase the tick parameter on the main
     *         thread. Consider using {@link #disableTrackingForPlayer(UUID)}
     *         for a sane default.
     *     </li>
     * </ul>
     *
     * <p>
     *      If you do not use "try with resources", call the <code>close</code>
     *      method upon completion to reactivate tracking.
     * </p>
     *
     * @param player The {@link UUID} of the player to disable tracking for.
     * @param time The time to disable tracking for, or {@link Duration#ZERO} to
     *      indicate next tick.
     * @return The {@link AutoCloseable} that will re-enable the tracking when done.
     */
    NoExceptionAutoClosable disableTrackingForPlayer(UUID player, Duration time);

    enum AFKNotificationResult {

        NOT_AFK {
            @Override
            public boolean isAFK() {
                return false;
            }
        },
        AFK_NOTIFIED {
            @Override
            public boolean isNotified() {
                return true;
            }
        },
        AFK_NOT_NOTIFIED;

        public boolean isAFK() {
            return true;
        }

        public boolean isNotified() {
            return false;
        }

    }

}
