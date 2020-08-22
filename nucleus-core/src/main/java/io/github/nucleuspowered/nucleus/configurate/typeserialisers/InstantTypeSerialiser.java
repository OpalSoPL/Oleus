/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.time.Instant;

public class InstantTypeSerialiser implements TypeSerializer<Instant> {

    @Override public Instant deserialize(final TypeToken<?> type, final ConfigurationNode value) {
        return Instant.ofEpochMilli(value.getLong());
    }

    @Override public void serialize(final TypeToken<?> type, final Instant obj, final ConfigurationNode value) {
        value.setValue(obj.toEpochMilli());
    }
}
