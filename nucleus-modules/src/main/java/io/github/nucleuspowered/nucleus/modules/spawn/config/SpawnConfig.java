/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class SpawnConfig {

    @Setting(value = "spawn-on-login")
    @LocalisedComment("config.spawn.onlogin")
    private boolean spawnOnLogin = false;

    @Setting(value = "use-safe-spawn")
    @LocalisedComment("config.spawn.safe")
    private boolean safeTeleport = true;

    @Setting(value = "global-spawn")
    @LocalisedComment("config.spawn.global.base")
    private GlobalSpawnConfig globalSpawn = new GlobalSpawnConfig();

    @Setting(value = "affect-bed-spawn")
    @LocalisedComment("config.spawn.bedspawn")
    private boolean redirectBedSpawn = true;

    @Setting(value = "spawn-on-login-exempt-worlds")
    @LocalisedComment("config.spawn.onloginsameworld")
    private List<String> spawnOnLoginExemptWorld = new ArrayList<>();

    @Setting(value = "per-world-permissions")
    @LocalisedComment("config.spawn.worlds")
    private boolean perWorldPerms = false;

    public boolean isSpawnOnLogin() {
        return this.spawnOnLogin;
    }

    public boolean isSafeTeleport() {
        return this.safeTeleport;
    }

    public GlobalSpawnConfig getGlobalSpawn() {
        return this.globalSpawn;
    }

    public boolean isRedirectBedSpawn() {
        return this.redirectBedSpawn;
    }

    public List<String> getOnLoginExemptWorlds() {
        return this.spawnOnLoginExemptWorld == null ? Collections.emptyList() : this.spawnOnLoginExemptWorld;
    }

    public boolean isPerWorldPerms() {
        return this.perWorldPerms;
    }
}
