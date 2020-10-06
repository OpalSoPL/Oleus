/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.config;

import org.spongepowered.api.world.teleport.TeleportHelper;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SafeTeleportConfig {

    @Setting
    private int width = TeleportHelper.DEFAULT_WIDTH;

    @Setting
    private int height = TeleportHelper.DEFAULT_HEIGHT;

    public int getWidth() {
        return Math.max(1, this.width);
    }

    public int getHeight() {
        return Math.max(1, this.height);
    }
}
