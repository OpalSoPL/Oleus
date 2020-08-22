/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.nickname.exception;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.util.TextMessageException;

public class NicknameException extends TextMessageException {

    private final Type type;

    public NicknameException(final Component message, final Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        /**
         * If a nickname is an IGN, but not their own
         */
        NOT_OWN_IGN,

        /**
         * The user has not got permission to use a style or colour
         */
        INVALID_STYLE_OR_COLOUR,

        /**
         * This nickname does not conform to the nickname in the config
         */
        INVALID_PATTERN,

        /**
         * This nickname is too short
         */
        TOO_SHORT,

        /**
         * This nickname is too long
         */
        TOO_LONG,

        /**
         * The nickname event was cancelled by a plugin
         */
        EVENT_CANCELLED,

        /**
         * The user cannot be found in the Nucleus system
         */
        NO_USER
    }
}
