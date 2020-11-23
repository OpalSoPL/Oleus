/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class NearConfig {

    @Setting(value = "max-radius")
    @LocalisedComment("config.playerinfo.near.maxradius")
    private int maxRadius = 200;

    public int getMaxRadius() {
        return this.maxRadius;
    }

}
