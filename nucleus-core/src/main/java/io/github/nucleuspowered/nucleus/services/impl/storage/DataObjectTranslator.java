/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.storage;

import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.configurate.AbstractConfigurateBackedDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.GeneralDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.UserDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.WorldDataObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A Configurate translator that allows for {@link AbstractConfigurateBackedDataObject}s to be translated.
 */
public final class DataObjectTranslator implements TypeSerializer<AbstractConfigurateBackedDataObject> {

    public static final DataObjectTranslator INSTANCE = new DataObjectTranslator();

    private DataObjectTranslator() {}

    @Nullable
    @Override
    public AbstractConfigurateBackedDataObject deserialize(@NonNull final Type type, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        AbstractConfigurateBackedDataObject ado = null;
        Class<?> clazz = null;
        if (type instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            clazz = (Class<?>) type;
        }

        if (clazz != null) {
            if (UserDataObject.class.isAssignableFrom(clazz)) {
                ado = new UserDataObject();
            } else if (WorldDataObject.class.isAssignableFrom(clazz)) {
                ado = new WorldDataObject();
            } else if (GeneralDataObject.class.isAssignableFrom(clazz)) {
                ado = new GeneralDataObject();
            }
        }

        if (ado != null) {
            ado.setBackingNode(value);
        }

        return ado;
    }

    @Override
    public void serialize(@NonNull final Type type, @Nullable final AbstractConfigurateBackedDataObject obj, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        if (obj != null) {
            value.setValue(obj.getBackingNode());
        }
    }
}
