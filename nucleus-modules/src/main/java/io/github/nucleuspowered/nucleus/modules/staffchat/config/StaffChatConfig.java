/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class StaffChatConfig {

    @Setting(value = "include-standard-chat-formatting")
    @LocalisedComment("config.staffchat.includestd")
    private boolean includeStandardChatFormatting = false;

    @Setting(value = "message-template")
    @LocalisedComment("config.staffchat.template")
    private String messageTemplate = "&b[STAFF] &r{{displayname}}&b: ";

    @Setting(value = "message-colour")
    @LocalisedComment("config.staffchat.colour")
    private String messageColour = "b";

    public String getMessageTemplate() {
        return this.messageTemplate;
    }

    public String getMessageColour() {
        if (this.messageColour.isEmpty() || !this.messageColour.matches("^[0-9a-f]")) {
            return "b";
        }

        return this.messageColour.substring(0, 1);
    }

    public boolean isIncludeStandardChatFormatting() {
        return this.includeStandardChatFormatting;
    }
}
