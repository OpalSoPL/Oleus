/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantTypeSerialiser implements TypeSerializer<Instant> {

    @Override
    public Instant deserialize(final Type type, final ConfigurationNode value) {
        return Instant.ofEpochMilli(value.getLong());
    }

    @Override
    public void serialize(final Type type, final Instant obj, final ConfigurationNode value) throws SerializationException {
        if (obj != null) {
            value.set(obj.toEpochMilli());
        }
    }

}
