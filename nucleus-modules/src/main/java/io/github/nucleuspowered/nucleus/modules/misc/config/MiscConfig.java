/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.config;


import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MiscConfig {

    @Setting(value = "max-speed")
    @LocalisedComment("config.misc.speed.max")
    private int maxSpeed = 5;

    public int getMaxSpeed() {
        return this.maxSpeed;
    }
}
