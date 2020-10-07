/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.deathmessage.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class DeathMessageConfig {

    @Setting(value = "enable-death-messages")
    @LocalisedComment("config.deathmessages.enable")
    private boolean enableDeathMessages = true;

    @Setting(value = "force-show-all-death-messages")
    @LocalisedComment("config.deathmessages.showall")
    private boolean forceForAll = true;

    public boolean isEnableDeathMessages() {
        return this.enableDeathMessages;
    }

    public boolean isForceForAll() {
        return this.forceForAll;
    }
}
