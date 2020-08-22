/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.staffchat;

import net.kyori.adventure.audience.Audience;

import java.awt.Component;

/**
 * Provides a way to get the Staff Chat message channel instance.
 */
public interface NucleusStaffChatService {

    /**
     * Sends a message to the Staff Chat channel.
     *
     * @param source The {@link Audience} that is sending this message.
     * @param message The message to send.
     */
    void sendMessageFrom(Audience source, Component message);

}
