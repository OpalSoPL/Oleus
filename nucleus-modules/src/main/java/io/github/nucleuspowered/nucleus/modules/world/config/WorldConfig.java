/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Optional;

@ConfigSerializable
public class WorldConfig {

    @Setting(value = "default-world-border-diameter")
    @LocalisedComment("config.world.defaultborder")
    private long worldBorderDefault = 0;

    @Setting(value = "separate-permissions")
    @LocalisedComment("config.worlds.separate")
    private boolean separatePermissions = false;

    @Setting(value = "enforce-gamemode-on-world-change")
    @LocalisedComment("config.worlds.gamemode")
    private boolean enforceGamemodeOnWorldChange = false;

    public boolean isEnforceGamemodeOnWorldChange() {
        return this.enforceGamemodeOnWorldChange;
    }

    public Optional<Long> getWorldBorderDefault() {
        if (this.worldBorderDefault < 1) {
            return Optional.empty();
        }

        return Optional.of(this.worldBorderDefault);
    }

    public boolean isSeparatePermissions() {
        return this.separatePermissions;
    }

}
