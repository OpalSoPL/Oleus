/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.util;

import io.github.nucleuspowered.nucleus.api.util.WorldPositionRotation;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.math.vector.Vector3d;

public final class WorldPositionRotationImpl implements WorldPositionRotation {

    private final Vector3d position;
    private final Vector3d rotation;
    private final ResourceKey resourceKey;

    public WorldPositionRotationImpl(final Vector3d position, final Vector3d rotation, final ResourceKey resourceKey) {
        this.position = position;
        this.rotation = rotation;
        this.resourceKey = resourceKey;
    }

    @Override public Vector3d getPosition() {
        return this.position;
    }

    @Override public Vector3d getRotation() {
        return this.rotation;
    }

    @Override public ResourceKey getResourceKey() {
        return this.resourceKey;
    }
}
