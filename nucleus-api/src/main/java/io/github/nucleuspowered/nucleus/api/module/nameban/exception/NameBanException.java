/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.nameban.exception;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.util.ComponentMessageException;

public class NameBanException extends ComponentMessageException {

    private final Reason reason;

    public NameBanException(final Component message, final Reason reason) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    public enum Reason {
        DISALLOWED_NAME,
        DOES_NOT_EXIST
    }
}
