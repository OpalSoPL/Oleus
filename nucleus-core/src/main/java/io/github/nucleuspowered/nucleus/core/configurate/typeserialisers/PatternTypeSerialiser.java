/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.configurate.typeserialisers;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public final class PatternTypeSerialiser implements TypeSerializer<Pattern> {

    @SuppressWarnings("ConstantConditions")
    @Override
    public Pattern deserialize(final Type type, final ConfigurationNode value) throws SerializationException {
        if (value.virtual()) {
            return null;
        }
        return Pattern.compile(value.getString());
    }

    @Override
    public void serialize(final Type type, final Pattern obj, final ConfigurationNode value) throws SerializationException {
        if (obj != null) {
            value.set(TypeToken.get(String.class), obj.pattern());
        }
    }
}
