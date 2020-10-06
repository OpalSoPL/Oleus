/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ChatLoggingConfig {

    @Setting(value = "enable-logging")
    @LocalisedComment("config.chatlog.enable")
    private boolean enableLog = false;

    @Setting(value = "log-chat")
    @LocalisedComment("config.chatlog.chat")
    private boolean logChat = true;

    @Setting(value = "log-messages")
    @LocalisedComment("config.chatlog.message")
    private boolean logMessages = true;

    @Setting(value = "log-mail")
    @LocalisedComment("config.chatlog.mail")
    private boolean logMail = false;

    public boolean isEnableLog() {
        return this.enableLog;
    }

    public boolean isLogChat() {
        return this.logChat;
    }

    public boolean isLogMessages() {
        return this.logMessages;
    }

    public boolean isLogMail() {
        return this.logMail;
    }
}
