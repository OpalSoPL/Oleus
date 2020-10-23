package io.github.nucleuspowered.nucleus.util.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class NucleusCollectors {

    public static <E, R extends E> Collector<E, List<R>, Collection<R>> toFilteredElementCollection(final Class<R> castTo) {
        return new Collector<E, List<R>, Collection<R>>() {

            @Override
            public Supplier<List<R>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<R>, E> accumulator() {
                return (list, input) -> {
                    if (castTo.isInstance(input)) {
                        list.add(castTo.cast(input));
                    }
                };
            }

            @Override
            public BinaryOperator<List<R>> combiner() {
                return (in1, in2) -> {
                    in1.addAll(in2);
                    return in1;
                };
            }

            @Override
            public Function<List<R>, Collection<R>> finisher() {
                return Collections::unmodifiableCollection;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.singleton(Characteristics.UNORDERED);
            }
        };
    }

    private NucleusCollectors() {
    }

}
