/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fly.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class FlyConfig {

    @Setting(value = "save-all-flystate-on-quit")
    @LocalisedComment("config.fly.stateonquit")
    private boolean saveOnQuit = true;

    @Setting(value = "find-safe-location-on-login")
    @LocalisedComment("config.fly.onlogin")
    private boolean findSafeOnLogin = true;

    @Setting(value = "require-fly-permission-on-login")
    @LocalisedComment("config.fly.permissiononlogin")
    private boolean permissionOnLogin = false;

    public boolean isSaveOnQuit() {
        return this.saveOnQuit;
    }

    public boolean isPermissionOnLogin() {
        return this.permissionOnLogin;
    }

    public boolean isFindSafeOnLogin() {
        return this.findSafeOnLogin;
    }
}
