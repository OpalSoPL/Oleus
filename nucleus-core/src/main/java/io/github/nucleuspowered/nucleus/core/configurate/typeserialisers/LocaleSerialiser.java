/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.configurate.typeserialisers;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Locale;

public class LocaleSerialiser implements TypeSerializer<Locale> {

    @Nullable
    @Override
    public Locale deserialize(@NonNull final Type type, @NonNull final ConfigurationNode value) {
        final Locale l = Locale.forLanguageTag(value.getString("und").replace("_", "-"));
        if (l.toString().isEmpty()) {
            return null;
        }
        return l;
    }

    @Override
    public void serialize(@NonNull final Type type, @Nullable final Locale obj, @NonNull final ConfigurationNode value)
            throws SerializationException {
        if (obj != null && !obj.toString().isEmpty()) {
            value.set(obj.toString());
        }
    }

}
