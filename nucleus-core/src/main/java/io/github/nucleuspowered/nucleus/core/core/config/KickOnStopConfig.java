/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class KickOnStopConfig {

    @Setting(value = "enabled")
    @LocalisedComment("config.core.kickonstop.flag")
    private boolean kickOnStop = false;

    @Setting("message")
    @LocalisedComment("config.core.kickonstop.message")
    private String kickOnStopMessage = "Server closed";

    public boolean isKickOnStop() {
        return this.kickOnStop;
    }

    public String getKickOnStopMessage() {
        return this.kickOnStopMessage;
    }
}
