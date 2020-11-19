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
