/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

import org.spongepowered.api.util.Tuple;

import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public final class Tuples {

    private Tuples() {}

    public static <A, B> Tuple<A, B> of(final A a, final B b) {
        return new Tuple<>(a, b);
    }

    public static <A, B> NullableTuple<A, B> ofNullable(final A a, final B b) {
        return new NullableTuple<>(a, b);
    }

    public static <A, B, C> Tri<A, B, C> of(final A a, final B b, final C c) {
        return new Tri<>(a, b, c);
    }

    public static <A, B, C, D> Quad<A, B, C, D> of(final A a, final B b, final C c, final D d) {
        return new Quad<>(a, b, c, d);
    }

    public static class NullableTuple<A, B> {

        public NullableTuple(@Nullable final A first, @Nullable final B second) {
            this.first = first;
            this.second = second;
        }

        @Nullable private final A first;
        @Nullable private final B second;

        public Optional<A> getFirst() {
            return Optional.ofNullable(this.first);
        }

        public Optional<B> getSecond() {
            return Optional.ofNullable(this.second);
        }

        public A getFirstUnwrapped() {
            return this.first;
        }

        public B getSecondUnwrapped() {
            return this.second;
        }

        public void mapIfPresent(final Consumer<A> firstConsumer, final Consumer<B> secondConsumer) {
            this.getFirst().ifPresent(firstConsumer);
            this.getSecond().ifPresent(secondConsumer);
        }
    }

    public static class Tri<A, B, C> {

        private final A first;
        private final B second;
        private final C third;

        private Tri(final A first, final B second, final C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() {
            return this.first;
        }

        public B getSecond() {
            return this.second;
        }

        public C getThird() {
            return this.third;
        }
    }

    public static class Quad<A, B, C, D> extends Tri<A, B, C> {

        private final D fourth;

        private Quad(final A first, final B second, final C third, final D fourth) {
            super(first, second, third);
            this.fourth = fourth;
        }

        public D getFourth() {
            return this.fourth;
        }
    }
}
