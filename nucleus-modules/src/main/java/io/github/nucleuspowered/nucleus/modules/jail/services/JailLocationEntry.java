/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.services;

import io.github.nucleuspowered.nucleus.api.module.jail.data.Jail;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

public final class JailLocationEntry implements Jail, NamedLocation {

    private final ResourceKey worldKey;
    private final Vector3d position;
    private final Vector3d rotation;
    private final String name;

    public JailLocationEntry(final NamedLocation location) {
        this(location.getResourceKey(), location.getPosition(), location.getRotation(), location.getName());
    }

    public JailLocationEntry(final ResourceKey worldKey, final Vector3d position, final Vector3d rotation, final String name) {
        this.worldKey = worldKey;
        this.position = position;
        this.rotation = rotation;
        this.name = name;
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
        return Optional.of(ServerLocation.of(this.worldKey, this.position));
    }

    @Override
    public String getName() {
        return this.name;
    }

}
