/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SeenConfig {

    @Setting(value = "require-extended-permission-for-module-info")
    @LocalisedComment("config.playerinfo.seen.extended")
    private boolean extendedPermRequired = false;

    public boolean isExtendedPermRequired() {
        return this.extendedPermRequired;
    }
}
