/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.data;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Optional;

public class WarpCategoryData implements WarpCategory {

    public WarpCategoryData(final String id, @Nullable final Component displayName, final Component description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    private final String id;
    @Nullable private final Component displayName;
    private final Component description;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Component getDisplayName() {
        return this.displayName == null ? Component.text(this.id) : this.displayName;
    }

    @Override
    public Optional<Component> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public Collection<Warp> getWarps() {
        return null;
    }
}
