/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban.config;

import io.github.nucleuspowered.nucleus.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class BanConfig {

    @Setting(value = "maximum-tempban-length")
    @LocalisedComment("config.tempban.maxtempbanlength")
    private long maxTempBanLength = 604800;

    @Setting(value = "ban-permission-levels")
    @LocalisedComment("config.ban.permissionlevel")
    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    public long getMaximumTempBanLength() {
        return this.maxTempBanLength;
    }

    public CommonPermissionLevelConfig getLevelConfig() {
        return this.levelConfig;
    }
}
