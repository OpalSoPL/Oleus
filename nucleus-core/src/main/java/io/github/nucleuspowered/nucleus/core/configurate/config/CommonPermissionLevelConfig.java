/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.configurate.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class CommonPermissionLevelConfig {

    @Setting(value = "use-permission-level")
    @LocalisedComment("config.common.permission-level")
    private boolean useLevels = false;

    @Setting(value = "can-affect-same-level")
    @LocalisedComment("config.common.same-level")
    private boolean canAffectSameLevel = false;

    public boolean isUseLevels() {
        return this.useLevels;
    }

    public boolean isCanAffectSameLevel() {
        return this.canAffectSameLevel;
    }
}
