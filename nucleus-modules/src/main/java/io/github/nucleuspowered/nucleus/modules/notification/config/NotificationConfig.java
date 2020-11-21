/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class NotificationConfig {

    @Setting(value = "broadcast-message-template")
    @LocalisedComment("config.broadcast.template")
    private BroadcastConfig broadcastMessage = new BroadcastConfig();

    @Setting(value = "title-defaults")
    @LocalisedComment("config.title.defaults")
    private TitleConfig titleConfig = new TitleConfig();

    public BroadcastConfig getBroadcastMessage() {
        return this.broadcastMessage;
    }

    public TitleConfig getTitleDefaults() {
        return this.titleConfig;
    }
}
