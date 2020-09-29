/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.datatypes;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.storage.WorldProperties;

import java.text.MessageFormat;
import java.util.Optional;

import javax.annotation.Nullable;

public class LocationData implements NamedLocation {

    private final String warpName;
    private final ResourceKey resourceKey;
    @Nullable private final WorldProperties worldProperties;
    private final Vector3d position;
    private final Vector3d rotation;

    public LocationData(final String name, final ResourceKey world, final Vector3d position, final Vector3d rotation) {
        this.rotation = rotation;
        this.position = position;
        this.warpName = name;
        this.resourceKey = world;
        this.worldProperties = Sponge.getServer().getWorldManager().getProperties(this.resourceKey).orElse(null);
    }

    public String getName() {
        return this.warpName;
    }

    @Override
    public ResourceKey getResourceKey() {
        return this.resourceKey;
    }

    @Override public Optional<WorldProperties> getWorldProperties() {
        return Optional.ofNullable(this.worldProperties);
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override public Vector3d getPosition() {
        return this.position;
    }

    @Override public Optional<ServerLocation> getLocation() {
        return Sponge.getServer().getWorldManager().getWorld(this.resourceKey).map(x -> x.getLocation(this.position));
    }

    public String toLocationString() {
        if (this.worldProperties == null) {
            return MessageFormat.format("name: {0}, no location", this.warpName);
        }

        return MessageFormat.format("name: {0}, world: {1}, x: {2}, y: {3}, z: {4}", this.warpName, this.resourceKey.asString(),
            (int) this.position.getX(), (int) this.position.getY(), (int) this.position.getZ());
    }
}
