/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.config;

import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.modules.warp.data.WarpCategoryData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class WarpCategorySerialiser implements TypeSerializer<WarpCategory> {

    private static final String DESCRIPTION_ID = "description";
    private static final String DISPLAY_NAME_ID = "displayName";

    @Override
    public WarpCategory deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final String description = node.node(DESCRIPTION_ID).getString();
        final String displayName = node.node(DISPLAY_NAME_ID).getString();
        return new WarpCategoryData(
                String.valueOf(node.key()),
                displayName == null ? Component.text(String.valueOf(node.key())) : GsonComponentSerializer.gson().deserialize(displayName),
                description == null ? null : GsonComponentSerializer.gson().deserialize(description)
        );
    }

    @Override public void serialize(final Type type, @Nullable final WarpCategory obj, final ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }
        if (obj.getDescription().isPresent()) {
            node.node(DESCRIPTION_ID).set(GsonComponentSerializer.gson().serialize(obj.getDescription().get()));
        }
        node.node(DISPLAY_NAME_ID).set(GsonComponentSerializer.gson().serialize(obj.getDisplayName()));
    }
}
