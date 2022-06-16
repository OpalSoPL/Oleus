/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.services;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class TPAResult {

    private final boolean success;
    private final @Nullable String key;
    private final @Nullable String name;

    public final static TPAResult SUCCESS = new TPAResult(true, null, null);

    public static TPAResult failure(final String key, final String name) {
        return new TPAResult(false, key, name);
    }

    private TPAResult(final boolean success, final @Nullable String key, final @Nullable String name) {
        this.success = success;
        this.key = key;
        this.name = name;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String key() {
        return this.key;
    }

    public String name() {
        return this.name;
    }

}
