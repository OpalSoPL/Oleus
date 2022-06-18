/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.keyed.impl;

import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.keyed.DataKey;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.keyed.IKeyedDataObject;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import io.vavr.Tuple;
import io.vavr.control.Option;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link Map} specific data key.
 *
 * @param <V> The value type of the {@link Map}.
 * @param <O> The {@link IKeyedDataObject} this will apply to.
 */
public class MappedDataKeyStringKeyed<V, O extends IKeyedDataObject<?>> extends AbstractDataKey<Map<String, V>, O>
        implements DataKey.StringKeyedMapKey<V, O> {

    private final Class<V> erasedType;
    private final DataKeyDeserialisers.Deserialiser<V> converter;

    private static <Value> Type createMapType(final TypeToken<Value> valueToken) {
        return TypeFactory.parameterizedClass(Map.class, String.class, valueToken.getType());
    }

    @SuppressWarnings("unchecked")
    public MappedDataKeyStringKeyed(final String[] key, final TypeToken<V> valueType, final Class<O> target, final @Nullable BiConsumer<DataQuery, DataView> transformer) {
        super(key, MappedDataKeyStringKeyed.createMapType(valueType), target, null, transformer);
        this.erasedType = (Class<V>) GenericTypeReflector.erase(valueType.getType());
        this.converter = DataKeyDeserialisers.getTypeFor(this.erasedType);
    }

    @Override
    public Optional<Map<String, V>> getFromDataView(final DataView dataView) {
        final Optional<DataView> viewOpt = dataView.getView(this.getDataQuery());
        if (viewOpt.isPresent()) {
            final DataView view = viewOpt.get().copy(DataView.SafetyMode.NO_DATA_CLONED);
            final Set<DataQuery> keys = view.keys(false);

            final Map<String, V> result = new HashMap<>();
            for (final DataQuery query : keys) {
                this.converter.scalar.apply(view, query).ifPresent(x -> result.put(query.last().asString('.'), x));
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }



}
