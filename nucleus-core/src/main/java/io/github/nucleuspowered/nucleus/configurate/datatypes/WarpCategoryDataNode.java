/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

import javax.annotation.Nullable;

@ConfigSerializable
public class WarpCategoryDataNode {

    public WarpCategoryDataNode() {
    }

    public WarpCategoryDataNode(@Nullable final String displayName, @Nullable final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @Setting
    @Nullable
    private String displayName = null;

    @Setting
    @Nullable
    private String description = null;

    public Optional<Text> getDisplayName() {
        if (this.displayName == null) {
            return Optional.empty();
        }
        return Optional.of(TextSerializers.JSON.deserialize(this.displayName));
    }

    public void setDisplayName(@Nullable final TextComponent displayName) {
        this.displayName = displayName == null ? null : TextSerializers.JSON.serialize(displayName);
    }

    public Optional<Text> getDescription() {
        if (this.description == null) {
            return Optional.empty();
        }
        return Optional.of(TextSerializers.JSON.deserialize(this.description));
    }

    public void setDescription(@Nullable final TextComponent description) {
        this.description = description == null ? null : TextSerializers.JSON.serialize(description);
    }
}
