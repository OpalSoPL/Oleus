/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.data;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

public class WarpData implements Warp {

    private final String category;
    private final Double cost;
    private final Component description;
    private final ResourceKey worldKey;
    private final Vector3d position;
    private final Vector3d rotation;
    private final String name;

    public WarpData(final String category,
                    final double cost,
                    final Component description,
                    final ResourceKey worldKey,
                    final Vector3d position,
                    final Vector3d rotation,
                    final String name) {
        this.category = category;
        this.cost = cost == 0 ? null : cost;
        this.description = description;
        this.worldKey = worldKey;
        this.position = position;
        this.rotation = rotation;
        this.name = name;
    }

    @Override
    public Optional<String> getCategory() {
        return Optional.ofNullable(this.category);
    }

    @Override
    public Optional<Double> getCost() {
        return Optional.ofNullable(this.cost);
    }

    @Override
    public Optional<Component> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public ResourceKey getResourceKey() {
        return this.worldKey;
    }

    @Override
    public Optional<WorldProperties> getWorldProperties() {
        return Sponge.getServer().getWorldManager().getProperties(this.worldKey);
    }

    @Override
    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override
    public Vector3d getPosition() {
        return this.position;
    }

    @Override
    public Optional<ServerLocation> getLocation() {
        return Sponge.getServer().getWorldManager().getWorld(this.worldKey).map(x -> ServerLocation.of(x, this.position));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
