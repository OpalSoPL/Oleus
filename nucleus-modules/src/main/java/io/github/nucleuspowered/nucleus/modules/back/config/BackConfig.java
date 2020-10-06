/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class BackConfig {

    @Setting(value = "only-same-dimension")
    @LocalisedComment("config.back.onlySameDimension")
    private boolean onlySameDimension = false;

    @Setting(value = "on-death")
    @LocalisedComment("config.back.ondeath")
    private boolean onDeath = true;

    @Setting(value = "on-teleport")
    @LocalisedComment("config.back.onteleport")
    private boolean onTeleport = true;

    @Setting(value = "on-portal")
    @LocalisedComment("config.back.onportal")
    private boolean onPortal = false;

    public boolean isOnDeath() {
        return this.onDeath;
    }

    public boolean isOnTeleport() {
        return this.onTeleport;
    }

    public boolean isOnPortal() {
        return this.onPortal;
    }

    public boolean isOnlySameDimension() {
        return this.onlySameDimension;
    }
}
