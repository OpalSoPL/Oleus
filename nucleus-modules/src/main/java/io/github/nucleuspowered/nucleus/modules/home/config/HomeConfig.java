/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class HomeConfig {

    @Setting(value = "use-safe-warp")
    @LocalisedComment("config.home.safeTeleport")
    private boolean safeTeleport = true;

    @Setting(value = "respawn-at-home")
    @LocalisedComment("config.home.respawnAtHome")
    private boolean respawnAtHome = false;

    @Setting(value = "prevent-home-count-overhang")
    @LocalisedComment("config.home.overhang")
    private boolean preventHomeCountOverhang = true;
    
    @Setting(value = "only-same-dimension")
    @LocalisedComment("config.home.onlySameDimension")
    private boolean onlySameDimension = false;

    public boolean isSafeTeleport() {
        return this.safeTeleport;
    }

    public boolean isRespawnAtHome() {
        return this.respawnAtHome;
    }

    public boolean isPreventHomeCountOverhang() {
        return this.preventHomeCountOverhang;
    }

    public boolean isOnlySameDimension() {
        return this.onlySameDimension;
    }
}
