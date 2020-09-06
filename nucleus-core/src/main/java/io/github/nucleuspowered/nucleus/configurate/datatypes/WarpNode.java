/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

@ConfigSerializable
public class WarpNode extends LocationNode {

    @Setting("cost")
    private double cost = -1;

    @Setting("category")
    @Nullable
    private String category = null;

    @Setting("description")
    @Nullable
    private Component description = null;

    public WarpNode() {
        super();
    }

    public WarpNode(final ResourceKey world, final Vector3d length, final Vector3d rotation, final double cost, final String category,
            final Component description) {
        super(world, length, rotation);
        this.cost = cost;
        this.category = category;
        this.description = description;
    }

    public WarpNode(final ServerLocation location, final Vector3d rotation) {
        this(location, rotation, -1);
    }

    private WarpNode(final ServerLocation location, final Vector3d rotation, final int cost) {
        super(location, rotation);
        this.cost = cost;
    }

    public WarpNode(final ServerLocation location) {
        this(location, -1);
    }

    private WarpNode(final ServerLocation location, final int cost) {
        super(location);
        this.cost = cost;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(final double cost) {
        if (cost < -1) {
            this.cost = -1;
        }

        this.cost = cost;
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(this.category);
    }

    public void setCategory(@Nullable final String category) {
        this.category = category;
    }

    public Component getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable final Component description) {
        this.description = description;
    }
}
