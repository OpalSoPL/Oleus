/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.config;

import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.DefaultValueSetting;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class MessagesConfig {

    @DefaultValueSetting(key = "on-afk", defaultValue = "&7*&f{{displayname}} &7has gone AFK.")
    private NucleusTextTemplateImpl afkMessage;

    @DefaultValueSetting(key = "on-return", defaultValue = "&7*&f{{displayname}} &7is no longer AFK.")
    private NucleusTextTemplateImpl returnAfkMessage;

    @DefaultValueSetting(key = "on-command", defaultValue = "&f{{displayname}} &7is currently AFK and may not respond quickly.")
    private NucleusTextTemplateImpl onCommand;

    @DefaultValueSetting(key = "on-kick", defaultValue = "&f{{displayname}} &7has been kicked for being AFK too long.")
    @LocalisedComment("config.afk.messagetobroadcastonkick")
    private NucleusTextTemplateImpl onKick;

    @DefaultValueSetting(key = "kick-message-to-subject", defaultValue = "You have been kicked for being AFK for too long.")
    @LocalisedComment("config.afk.playerkicked")
    private NucleusTextTemplateImpl kickMessage;

    public NucleusTextTemplateImpl getAfkMessage() {
        return this.afkMessage;
    }

    public NucleusTextTemplateImpl getReturnAfkMessage() {
        return this.returnAfkMessage;
    }

    public NucleusTextTemplateImpl getOnCommand() {
        return this.onCommand;
    }

    public NucleusTextTemplateImpl getOnKick() {
        return this.onKick;
    }

    public NucleusTextTemplateImpl getKickMessage() {
        return this.kickMessage;
    }
}
