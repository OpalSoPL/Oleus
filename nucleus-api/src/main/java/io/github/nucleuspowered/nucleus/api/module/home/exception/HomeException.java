/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.home.exception;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.util.TextMessageException;

/**
 * Thrown when a home related action fails.
 */
public final class HomeException extends TextMessageException {

    private final Reasons reason;

    public HomeException(final Component message, final Reasons reasons) {
        super(message);
        this.reason = reasons;
    }

    public Reasons getReason() {
        return this.reason;
    }

    public enum Reasons {

        /**
         * The home point does not exists
         */
        DOES_NOT_EXIST,

        /**
         * The home name is invalid
         */
        INVALID_NAME,

        /**
         * The home exists, but the location is not valid. Usually
         * due to a world not being loaded, or having been removed.
         */
        INVALID_LOCATION,

        /**
         * The maximum number of permitted homes has already
         * been allocated.
         */
        LIMIT_REACHED,

        /**
         * A plugin cancelled the event.
         */
        PLUGIN_CANCELLED,

        /**
         * An unknown error occurred.
         */
        UNKNOWN;
    }

}
