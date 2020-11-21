/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message.event;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

/**
 * Fired when a helpop message is sent.
 */
public interface NucleusHelpOpEvent extends Cancellable, Event {

    /**
     * Gets the message sent to the HelpOp channel.
     *
     * @return The message
     */
    String getMessage();

}
