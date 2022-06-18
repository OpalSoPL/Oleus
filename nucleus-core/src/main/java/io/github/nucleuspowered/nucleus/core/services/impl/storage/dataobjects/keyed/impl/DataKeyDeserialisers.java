/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.keyed.impl;

import io.leangen.geantyref.GenericTypeReflector;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataView;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class DataKeyDeserialisers {

    private static Map<Class<?>, Deserialiser<?>> types =
            HashMap.ofEntries(
                    Map.entry(Integer.class, new Deserialiser<>(DataView::getInt, DataView::getIntegerList)),
                    Map.entry(String.class, new Deserialiser<>(DataView::getString, DataView::getStringList)),
                    Map.entry(Double.class, new Deserialiser<>(DataView::getDouble, DataView::getDoubleList))
            );

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <R> Deserialiser<R> defaultType(final Class<R> type) {
        final Optional<DataBuilder<?>> builder = Sponge.dataManager().builder((Class) type);
        if (builder.isPresent()) {
            final DataBuilder<?> b = builder.get();
            return new Deserialiser<>(
                    (dataView, dataQuery) -> (Optional<R>) dataView.getView(dataQuery).flatMap(b::build),
                    (dataView, dataQuery) ->
                            dataView.getViewList(dataQuery)
                                    .map(viewList ->
                                            viewList.stream().map(y -> (R) b.build(y).orElse(null)).filter(Objects::nonNull)
                                                    .collect(Collectors.toList())
                                    )
            );
        }

        final Optional<DataTranslator<R>> translator = Sponge.dataManager().translator(type);
        if (translator.isPresent()) {
            final DataTranslator<R> t = translator.get();
            return new Deserialiser<R>(
                    (dataView, dataQuery) -> dataView.getView(dataQuery).map(t::translate),
                    (dataView, dataQuery) -> dataView.getViewList(dataQuery)
                            .map(x -> x.stream().map(t::translate).collect(Collectors.toList()))
            );
        } else if (DataSerializable.class.isAssignableFrom(type)) {
            return new Deserialiser<R>(
                    (dataView, dataQuery) -> dataView.getSerializable(dataQuery, (Class) type),
                    (dataView, dataQuery) -> dataView.getSerializableList(dataQuery, (Class) type)
            );
        }
        return new Deserialiser<R>(
                (dataView, dataQuery) -> dataView.getObject(dataQuery, (Class) type),
                (dataView, dataQuery) -> dataView.getObjectList(dataQuery, (Class) type)
        );
    }

    @SuppressWarnings("unchecked")
    public static <R> Deserialiser<R> getTypeFor(final Type type) {
        final Class<R> erasedType = (Class<R>) GenericTypeReflector.erase(type);
        return (Deserialiser<R>) types.getOrElse(erasedType, DataKeyDeserialisers.defaultType(erasedType));
    }

    public final static class Deserialiser<R> {

        public final BiFunction<DataView, DataQuery, Optional<R>> scalar;
        public final BiFunction<DataView, DataQuery, Optional<List<R>>> list;

        public Deserialiser(
                final BiFunction<DataView, DataQuery, Optional<R>> scalar,
                final BiFunction<DataView, DataQuery, Optional<List<R>>> list) {
            this.scalar = scalar;
            this.list = list;
        }
    }

}
