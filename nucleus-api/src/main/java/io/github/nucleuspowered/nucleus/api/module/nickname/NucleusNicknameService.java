/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.nickname;

import io.github.nucleuspowered.nucleus.api.module.nickname.exception.NicknameException;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Allows plugins to set and inspect a {@link User}'s current nickname.
 */
public interface NucleusNicknameService {

    /**
     * Gets the current nickname for a user with a given {@link UUID},
     * including the prefix, if it exists.
     *
     * @param user The {@link UUID} of the user to inspect.
     * @return The nickname in {@link Component} form, if it exists.
     */
    Optional<Component> getNicknameWithPrefix(UUID user);

    /**
     * Gets the current nickname for a user with prefix, if it exists.
     *
     * @param user The {@link User} to inspect.
     * @return The nickname in {@link Component} form, if it exists.
     */
    Optional<Component> getNicknameWithPrefix(User user);

    /**
     * Gets the current nickname for a user, if it exists.
     *
     * @param user The {@link User} to inspect.
     * @return The nickname in {@link Component} form, if it exists.
     */
    Optional<Component> getNickname(User user);

    /**
     * Gets the current nickname for a user, if it exists.
     *
     * @param user The {@link UUID} of the user to inspect.
     * @return The nickname in {@link Component} form, if it exists.
     */
    Optional<Component> getNickname(UUID user);

    /**
     * Sets a user's nickname.
     *
     * @param user The {@link User} to change the nickname of
     * @param nickname The nickname, or {@code null} to remove it.
     * @throws NicknameException if the nickname could not be set.
     */
    default void setNickname(final UUID user, @Nullable final Component nickname) throws NicknameException {
        this.setNickname(user, nickname, false);
    }

    /**
     * Sets a user's nickname.
     *
     * @param user The {@link User} to change the nickname of
     * @param nickname The nickname, or {@code null} to remove it.
     * @param bypassRestrictions Whether to bypass the configured restrictions.
     * @throws NicknameException if the nickname could not be set.
     */
    void setNickname(UUID user, @Nullable Component nickname, boolean bypassRestrictions) throws NicknameException;

    /**
     * Removes the nickname for the specified user.
     *
     * @param user The nickname to set.
     * @throws NicknameException if the nickname could not be set.
     */
    default void removeNickname(final UUID user) throws NicknameException {
        this.setNickname(user, null);
    }

}
