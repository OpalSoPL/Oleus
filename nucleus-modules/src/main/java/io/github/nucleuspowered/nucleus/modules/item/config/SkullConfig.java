/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SkullConfig {

    @Setting(value = "use-minecraft-command")
    @LocalisedComment("config.item.skullcompat")
    private boolean useMinecraftCommand = false;

    @Setting(value = "spawn-limit")
    @LocalisedComment("config.item.skullspawnlimit")
    private int skullLimit = -1;

    public boolean isUseMinecraftCommand() {
        return this.useMinecraftCommand;
    }

    public int getSkullLimit() {
        return this.skullLimit <= 0 ? Integer.MAX_VALUE : this.skullLimit;
    }
}
