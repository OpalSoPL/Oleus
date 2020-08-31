/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message.target;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

/**
 * The target of a private message.
 */
public interface MessageTarget {

    /**
     * Called when this target recieves a message.
     *
     * @param userID The {@link UUID} of the user who sent the message, or
     *  {@code null} if the message came from a non-user.
     * @param message The message.
     */
    void receiveMessageFrom(@Nullable final UUID userID, final String message);

}
