/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommonPermissionLevelConfig {

    @Setting(value = "use-permission-level", comment = "config.common.permission-level")
private boolean useLevels = false;

    @Setting(value = "can-affect-same-level", comment = "config.common.same-level")
private boolean canAffectSameLevel = false;

    public boolean isUseLevels() {
        return this.useLevels;
    }

    public boolean isCanAffectSameLevel() {
        return this.canAffectSameLevel;
    }
}
