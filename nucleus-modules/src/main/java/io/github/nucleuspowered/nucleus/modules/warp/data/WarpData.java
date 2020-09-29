/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.data;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;
import java.util.UUID;

public class WarpData implements Warp {

    private final String category;
    private final Double cost;
    private final Component description;
    private final UUID worldPropertiesUUID;
    private final Vector3d position;
    private final Vector3d rotation;
    private final String name;

    public WarpData(final String category,
                    final double cost,
                    final Component description,
                    final UUID worldPropertiesUUID,
                    final Vector3d position,
                    final Vector3d rotation,
                    final String name) {
        this.category = category;
        this.cost = cost == 0 ? null : cost;
        this.description = description;
        this.worldPropertiesUUID = worldPropertiesUUID;
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
    public UUID getResourceKey() {
        return this.worldPropertiesUUID;
    }

    @Override
    public Optional<WorldProperties> getWorldProperties() {
        return Sponge.getServer().getWorldProperties(this.worldPropertiesUUID);
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
        return Sponge.getServer().getWorld(this.worldPropertiesUUID).map(x -> new Location<>(x, this.position));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
