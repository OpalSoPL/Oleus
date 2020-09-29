/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import org.spongepowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.datatypes.LocationData;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NamedLocationSerialiser implements TypeSerializer<NamedLocation> {

    private static final Map<TypeToken<? extends NamedLocation>, TypeSerializer<? extends NamedLocation>> providers = new HashMap<>();

    public static <T extends NamedLocation> void register(final TypeToken<T> clazz, final TypeSerializer<T> typeSerializer) {
        if (!NamedLocationSerialiser.providers.containsKey(clazz)) {
            NamedLocationSerialiser.providers.put(clazz, typeSerializer);
        }
    }

    @Nullable
    @Override
    public NamedLocation deserialize(@NonNull final TypeToken<?> type, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        for (final TypeToken<?> t : NamedLocationSerialiser.providers.keySet()) {
            if (type.isSubtypeOf(t)) {
                return NamedLocationSerialiser.providers.get(t).deserialize(type, value);
            }
        }

        final Vector3d pos = getPosition(value);
        final Vector3d rot = getRotation(value);

        return new LocationData(
                getName(value),
                getWorldUUID(value),
                pos,
                rot
        );
    }

    @Override
    public void serialize(@NonNull final TypeToken<?> type, @Nullable final NamedLocation obj, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        if (obj == null) {
            return;
        }

        for (final TypeToken<?> t : NamedLocationSerialiser.providers.keySet()) {
            if (type.isSubtypeOf(t)) {
                // Generic abuse.
                ((TypeSerializer) NamedLocationSerialiser.providers.get(t)).serialize(type, obj, value);
                return;
            }
        }

        serializeLocation(obj, value);
    }

    public static String getName(final ConfigurationNode value) {
        return value.getNode("name").getString(String.valueOf(value.getKey()));
    }

    public static UUID getWorldUUID(final ConfigurationNode value) throws ObjectMappingException {
        return value.getNode("world").getValue(TypeTokens.UUID);
    }

    public static Vector3d getPosition(final ConfigurationNode value) {
        return new Vector3d(
                value.getNode("x").getDouble(),
                value.getNode("y").getDouble(),
                value.getNode("z").getDouble()
        );
    }

    public static Vector3d getRotation(final ConfigurationNode value) {
        return new Vector3d(
                value.getNode("rotx").getDouble(),
                value.getNode("roty").getDouble(),
                value.getNode("rotz").getDouble()
        );
    }

    public static void serializeLocation(final NamedLocation obj, final ConfigurationNode value) throws ObjectMappingException {
        value.getNode("world").setValue(TypeTokens.UUID, obj.getResourceKey());

        value.getNode("x").setValue(obj.getPosition().getX());
        value.getNode("y").setValue(obj.getPosition().getY());
        value.getNode("z").setValue(obj.getPosition().getZ());

        value.getNode("rotx").setValue(obj.getRotation().getX());
        value.getNode("roty").setValue(obj.getRotation().getY());
        value.getNode("rotz").setValue(obj.getRotation().getZ());

        value.getNode("name").setValue(obj.getName());
    }
}
