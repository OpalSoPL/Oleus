/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.teleport.data;

public enum TeleportResult {

    SUCCESS(true),
    FAIL_NO_LOCATION,
    FAIL_CANCELLED;

    // ----

    private final boolean successState;

    TeleportResult() {
        this(false);
    }

    TeleportResult(final boolean successState) {
        this.successState = successState;
    }

    public boolean isSuccessful() {
        return this.successState;
    }

}
