/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

public class LazyLoadFunction<I, T> implements Function<I, T> {

    @Nullable private T entry;
    private final Function<I, T> creator;

    public LazyLoadFunction(final Function<I, T> t) {
        this.creator = t;
    }

    @Override
    public final T apply(final I i) {
        if (this.entry == null) {
            this.entry = this.creator.apply(i);
        }

        return this.entry;
    }
}
