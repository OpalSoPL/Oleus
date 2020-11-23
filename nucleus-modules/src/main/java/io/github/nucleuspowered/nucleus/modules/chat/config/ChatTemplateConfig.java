/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat.config;

import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.DefaultValueSetting;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ChatTemplateConfig {

    @Setting(value = "default-chatcolour")
    @LocalisedComment("config.chat.template.chatcolour")
    private String chatcolour = "";

    @Setting(value = "default-chatstyle")
    @LocalisedComment("config.chat.template.chatstyle")
    private String chatstyle = "";

    @Setting(value = "default-namecolour")
    @LocalisedComment("config.chat.template.namecolour")
    private String namecolour = "";

    @Setting(value = "default-namestyle")
    @LocalisedComment("config.chat.template.namestyle")
    private String namestyle = "";

    @DefaultValueSetting(key = "prefix", defaultValue = "{{prefix:s}}{{displayname}}{{suffix}}&f: ")
    @LocalisedComment("config.chat.template.prefix")
    private NucleusTextTemplateImpl prefix;

    @Setting
    @LocalisedComment("config.chat.template.suffix")
    private NucleusTextTemplateImpl suffix;

    public NucleusTextTemplateImpl getPrefix() {
        return this.prefix;
    }

    public NucleusTextTemplateImpl getSuffix() {
        return this.suffix;
    }

    public String getChatcolour() {
        return this.chatcolour;
    }

    public String getChatstyle() {
        return this.chatstyle;
    }

    public String getNamecolour() {
        return this.namecolour;
    }

    public String getNamestyle() {
        return this.namestyle;
    }
}
