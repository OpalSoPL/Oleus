/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message;

import io.github.nucleuspowered.nucleus.api.module.message.target.CustomMessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.SystemMessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.UserMessageTarget;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A service that contains message related APIs.
 */
public interface NucleusPrivateMessagingService {

    /**
     * Returns whether the user is able to see all private messages sent on the server. This indicates that the user
     * has the correct permission AND has activated it.
     *
     * @param uuid The {@link UUID} of the user to check.
     * @return <code>true</code> if the user has Social Spy enabled.
     */
    boolean isSocialSpy(UUID uuid);

    /**
     * Returns whether the server is using social spy levels.
     *
     * @return <code>true</code> if so.
     */
    boolean isUsingSocialSpyLevels();

    /**
     * If using social spy levels, this returns whether those on the same social spy level as the participants of a message can read a message.
     *
     * <p>
     *     In the following scenarios, Alice has a social spy level of 5, and Bob a social spy level of 10.
     * </p>
     * <p>
     *     If this method returns <code>true</code>, and Alice sends a message to Bob, Eve must have a level of 10 or above, to see the message. If
     *     Eve has a level of 9 or below, then because Bob's level is higher than hers, she <em>cannot</em> see the message.
     * </p>
     * <p>
     *     If this method returns <code>false</code>, and Alice sends a message to Bob, Eve must have a level of <strong>11</strong> or above, to see
     *     the message. If Eve has a level of 10 or below, then because Bob's level is <strong>the same</strong> than hers, she <em>cannot</em> see
     *     the message.
     * </p>
     *
     * @return Whether the server allows those with the same social spy level to spy on each other.
     */
    boolean canSpySameLevel();

    /**
     * Gets the social spy level for any message targets registered using
     * {@link #registerMessageTarget(CustomMessageTarget)}.
     *
     * @return The target's social spy level.
     */
    int getCustomTargetLevel();

    /**
     * Gets the social spy level the server is assigned. Typically (but not always) this is {@link Integer#MAX_VALUE}, or zero if levels aren't
     * enabled.
     *
     * @return The console/server's social spy level.
     */
    int getServerLevel();

    /**
     * Gets the social spy level the user is assigned. This is zero by default.
     *
     * @param user The user to check.
     * @return The level.
     */
    int getSocialSpyLevel(UUID user);

    /**
     * Returns whether the specified user can toggle social spy {@link Tristate#UNDEFINED}, or whether they are forced to use social spy {@link
     * Tristate#TRUE}, or do not have permission to do so {@link Tristate#FALSE}.
     *
     * @param user The use to check.
     * @return A {@link Tristate} that indicates what state the user might be forced into.
     */
    Tristate forcedSocialSpyState(UUID user);

    /**
     * Sets whether the user is able to see all private messages on the server. This method will return whether the
     * system has fulfilled the request.
     *
     * @param user The {@link User}
     * @param isSocialSpy <code>true</code> to turn Social Spy on, <code>false</code> otherwise.
     * @return <code>true</code> if the change was fulfilled, <code>false</code> if the user does not have permission
     */
    boolean setSocialSpy(UUID user, boolean isSocialSpy);

    /**
     * Returns whether the specified user can spy on <strong>all</strong> of the specified sources.
     *
     * <p>
     *     This will return <code>false</code> if the spying user is also in the list of users to spy on.
     * </p>
     *
     * @param spyingUser The user that will be spying.
     * @param sourcesToSpyOn The {@link UUID}s of the sources to spy upon.
     * @return <code>true</code> if the user can spy on <strong>all</strong> the users.
     * @throws IllegalArgumentException thrown if no {@link UUID}s are supplied.
     */
    boolean canSpyOn(UUID spyingUser, MessageTarget... sourcesToSpyOn) throws IllegalArgumentException;

    /**
     * Returns the {@link UUID}s of players that are online and can spy on
     * <strong>all</strong> of the specified sources.
     *
     * <p>
     *     This will not return players in the list of users to spy on.
     * </p>
     *
     * @param includeConsole Whether to include the console in the returned {@link Set}.
     * @param sourcesToSpyOn The {@link UUID}s to spy upon.
     * @return A {@link Set} of {@link UUID}s that can spy upon the specified users.
     * @throws IllegalArgumentException thrown if no {@link UUID}s are supplied.
     */
    Set<UUID> onlinePlayersCanSpyOn(boolean includeConsole, MessageTarget... sourcesToSpyOn) throws IllegalArgumentException;

    /**
     * Sends a message as the sender to the receiver. Takes a string to mirror what the command would do.
     *
     * @param sender The sender.
     * @param receiver The receiver.
     * @param message The message to send.
     * @return <code>true</code> if the message was sent, <code>false</code> otherwise.
     */
    boolean sendMessage(MessageTarget sender, MessageTarget receiver, String message);

    /**
     * Gets the {@link SystemMessageTarget}
     *
     * @return The system target
     */
    SystemMessageTarget getSystemMessageTarget();

    /**
     * Gets the {@link UserMessageTarget} for the given {@link UUID}
     *
     * @return The {@link UserMessageTarget}
     */
    UserMessageTarget getUserMessageTarget(UUID uuid);

    /**
     * Gets the {@link CustomMessageTarget} for the given identifier.
     *
     * <p>This identifier should not be prefixed with #.</p>
     *
     * @return The {@link CustomMessageTarget}
     */
    CustomMessageTarget getCustomMessageTarget(String identifier);

    /**
     * Gets the current {@link MessageTarget} that the given
     * {@link MessageTarget} will reply to if {@code /r} is used.
     *
     * @param target The target.
     * @return The target to reply to, if any.
     */
    Optional<MessageTarget> getCurrentReplyTarget(MessageTarget target);

    /**
     * Gets the current {@link MessageTarget} that the user associated with the
     * given {@link UUID} will reply to if {@code /r} is used.
     *
     * @param user The UUID of the user.
     * @return The target to reply to, if any.
     */
    default Optional<MessageTarget> getCurrentReplyTarget(final UUID user) {
        return this.getCurrentReplyTarget(this.getUserMessageTarget(user));
    }

    /**
     * Sets the reply target for the given {@link MessageTarget} to the
     * given {@link MessageTarget}.
     *
     * @param source The target to change the reply target for
     * @param newTarget The target who the source will now reply to
     */
    void setReplyTarget(MessageTarget source, MessageTarget newTarget);

    /**
     * Removes the reply target for the given {@link MessageTarget}, such that
     * the target will not reply to anyone if {@code /r} is used.
     *
     * @param source The target to clear the reply target for
     */
    void clearReplyTarget(MessageTarget source);

    /**
     * Registers a message target that players can message.
     *
     * <p>
     *     A message target is, perhaps unsurprisingly, a valid target for messaging. Players can
     *     message this target using <code>/m #&lt;targetName&gt; [message]</code>
     * </p>
     * <p>
     *     Message targets <em>cannot</em> be ignored. They are mostly intended for bot use.
     * </p>
     *
     * @param messageTarget The {@link CustomMessageTarget}
     */
    void registerMessageTarget(CustomMessageTarget messageTarget)
        throws NullPointerException, IllegalArgumentException, IllegalStateException;
}
