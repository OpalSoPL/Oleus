/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.datatypes;

import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class UUIDTransform {

    private final UUID world;
    private final Vector3d position;
    private final Vector3d rotation;

    public UUIDTransform(final Transform<World> worldTransform) {
        this(
                worldTransform.getExtent().getUniqueId(),
                worldTransform.getPosition(),
                worldTransform.getRotation()
        );
    }

    public UUIDTransform(final UUID world, final Vector3d position, final Vector3d rotation) {
        this.world = world;
        this.position = position;
        this.rotation = rotation;
    }

    public Optional<World> getWorld() {
        return Sponge.getServer().getWorld(this.world);
    }

    public Optional<World> loadWorld() {
        return Sponge.getServer().loadWorld(this.world);
    }

    public UUID getWorldUUID() {
        return this.world;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public Optional<Transform<World>> getTransform() {
        return this.getWorld().map(x -> new Transform<>(x, this.position, this.rotation));
    }

    public Optional<Transform<World>> loadTransform() {
        return this.loadWorld().map(x -> new Transform<>(x, this.position, this.rotation));
    }

}
