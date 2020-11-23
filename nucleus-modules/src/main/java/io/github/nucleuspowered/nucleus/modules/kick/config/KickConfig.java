/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kick.config;

import io.github.nucleuspowered.nucleus.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class KickConfig {

    @Setting(value = "kick-permission-levels")
    @LocalisedComment("config.kick.permissionlevel")
    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    public CommonPermissionLevelConfig getLevelConfig() {
        return this.levelConfig;
    }
}
