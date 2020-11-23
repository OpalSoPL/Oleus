/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.config;

import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.DefaultValueSetting;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class KickOnStopConfig {

    @Setting(value = "enabled")
    @LocalisedComment("config.core.kickonstop.flag")
    private boolean kickOnStop = false;

    @DefaultValueSetting(key = "message", defaultValue = "Server closed")
    @LocalisedComment("config.core.kickonstop.message")
    private NucleusTextTemplateImpl kickOnStopMessage;

    public boolean isKickOnStop() {
        return this.kickOnStop;
    }

    public NucleusTextTemplateImpl getKickOnStopMessage() {
        return this.kickOnStopMessage;
    }
}
