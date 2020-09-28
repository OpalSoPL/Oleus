/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class WarpSerialiser implements TypeSerializer<Warp> {

    public static final WarpSerialiser INSTANCE = new WarpSerialiser();

    private WarpSerialiser() {}

    @Nullable
    @Override
    public Warp deserialize(@NonNull final TypeToken<?> type, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        final String desc = value.getNode("description").getString();
        Component res = null;
        if (desc != null) {
            res = GsonComponentSerializer.gson().deserialize(desc);
        }

        return new WarpData(
                value.getNode("category").getString(),
                value.getNode("cost").getDouble(0d),
                res,
                NamedLocationSerialiser.getWorldUUID(value),
                NamedLocationSerialiser.getPosition(value),
                NamedLocationSerialiser.getRotation(value),
                NamedLocationSerialiser.getName(value)
        );
    }

    @Override
    public void serialize(@NonNull final TypeToken<?> type, @Nullable final Warp obj, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        if (obj == null) {
            return;
        }
        NamedLocationSerialiser.serializeLocation(obj, value);
        obj.getCategory().ifPresent(x -> value.getNode("category").setValue(x));
        obj.getCost().ifPresent(x -> value.getNode("cost").setValue(x));
    }
}
