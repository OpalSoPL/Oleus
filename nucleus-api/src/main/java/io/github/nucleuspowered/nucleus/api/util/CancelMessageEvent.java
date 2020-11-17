/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.util;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Optional;

public interface CancelMessageEvent extends Cancellable, Event {

    /**
     * The message to send to the player if the event is cancelled, if any.
     *
     * @return The message.
     */
    Optional<Component> getCancelMessage();

    /**
     * The message to display to the user if the event is cancelled, or <code>null</code> to clear.
     *
     * @param message The message.
     */
    void setCancelMessage(@Nullable Component message);
}
