/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.config;

import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.DefaultValueSetting;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MessagesConfig {

    @Setting(value = "on-afk")
    private String afkMessage = "&7*&f{{displayname}} &7has gone AFK.";

    @Setting(value = "on-return")
    private String returnAfkMessage = "&7*&f{{displayname}} &7has is no longer AFK.";

    @Setting(value = "on-command")
    private String onCommand = "&f{{displayname}} &7is currently AFK and may not respond quickly.";

    @Setting(value = "on-kick")
    @LocalisedComment("config.afk.messagetobroadcastonkick")
    private String onKick = "&f{{displayname}} &7has been kicked for being AFK too long.";

    @Setting(value = "kick-message-to-subject")
    @LocalisedComment("config.afk.playerkicked")
    private String kickMessage = "You have been kicked for being AFK for too long.";

    public String getAfkMessage() {
        return this.afkMessage;
    }

    public String getReturnAfkMessage() {
        return this.returnAfkMessage;
    }

    public String getOnCommand() {
        return this.onCommand;
    }

    public String getOnKick() {
        return this.onKick;
    }

    public String getKickMessage() {
        return this.kickMessage;
    }
}
