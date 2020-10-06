/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.storage.dataobjects.keyed;

import com.google.common.reflect.TypeParameter;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A {@link List} specific data key.
 *
 * @param <R> The inner type of the {@link List}.
 * @param <O> The {@link IKeyedDataObject} this will apply to.
 */
public class ListDataKey<R, O extends IKeyedDataObject<?>> extends DataKeyImpl<List<R>, O>
        implements DataKey.ListKey<R, O>{

    private final TypeToken<R> innerType;

    private static <S> Type createListToken(final TypeToken<S> innerToken) {
        return TypeFactory.parameterizedClass(List.class, innerToken.getType());
    }

    public ListDataKey(final String[] key, final TypeToken<R> type, final Class<O> target) {
        super(key, ListDataKey.createListToken(type), target, null);
        this.innerType = type;
    }

}
