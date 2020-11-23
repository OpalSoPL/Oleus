/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Optional;

@ConfigSerializable
public class GlobalSpawnConfig {

    @Setting(value = "on-respawn")
    @LocalisedComment("config.spawn.global.onrespawn")
    private boolean onRespawn = false;

    @Setting(value = "on-spawn-command")
    @LocalisedComment("config.spawn.global.oncommand")
    private boolean onSpawnCommand = false;

    @Setting(value = "on-login")
    @LocalisedComment("config.spawn.global.onlogin")
    private boolean onLogin = false;

    @Setting(value = "target-spawn-world")
    @LocalisedComment("config.spawn.global.target")
    private String spawnWorld = "world";

    public boolean isOnRespawn() {
        return this.onRespawn;
    }

    public boolean isOnSpawnCommand() {
        return this.onSpawnCommand;
    }

    public boolean isOnLogin() {
        return this.onLogin;
    }

    public Optional<WorldProperties> getWorld() {
        Optional<WorldProperties> ow = Sponge.getServer().getWorldManager().getProperties(ResourceKey.resolve(this.spawnWorld));
        if (!ow.isPresent()) {
            ow = Sponge.getServer().getWorldManager().getDefaultProperties();
        }

        return ow;
    }
}
