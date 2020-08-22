/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.storage.util;

import java.util.Optional;

public interface ThrownSupplier<I, X extends Throwable> {

    I get() throws X;

    default I getUnchecked() {
        try {
            return this.get();
        } catch (final Throwable x) {
            throw new RuntimeException(x);
        }
    }

    default Optional<I> asOptional() {
        try {
            return Optional.of(this.get());
        } catch (final Throwable x) {
            return Optional.empty();
        }
    }
}
