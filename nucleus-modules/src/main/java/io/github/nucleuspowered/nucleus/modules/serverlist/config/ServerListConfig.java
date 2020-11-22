/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.serverlist.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class ServerListConfig {

    @Setting(value = "modify-server-list-messages")
    @LocalisedComment("config.serverlist.modify")
    private ServerListSelection modifyServerList = ServerListSelection.FALSE;

    @Setting(value = "hide-vanished-players")
    @LocalisedComment("config.serverlist.hidevanished")
    private boolean hideVanishedPlayers = false;

    @Setting(value = "hide-player-count")
    @LocalisedComment("config.serverlist.hideall")
    private boolean hidePlayerCount = false;

    @Setting(value = "server-list-messages")
    @LocalisedComment("config.serverlist.messages")
    public List<String> messages = Collections.singletonList(
            "&bWelcome to the server!\n&cCome join us!"
    );

    @Setting(value = "whitelist-server-list-messages")
    @LocalisedComment("config.serverlist.whitelistmessages")
    public List<String> whitelist = Collections.emptyList();

    public boolean isModifyServerList() {
        return this.modifyServerList == ServerListSelection.TRUE;
    }

    public boolean isHideVanishedPlayers() {
        return this.hideVanishedPlayers;
    }

    public boolean isHidePlayerCount() {
        return this.hidePlayerCount;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public List<String> getWhitelist() {
        return this.whitelist;
    }

    public boolean enableListener() {
        return this.modifyServerList == ServerListSelection.TRUE || this.hideVanishedPlayers || this.hidePlayerCount;
    }

    public boolean enableWhitelistListener() {
        return this.modifyServerList == ServerListSelection.WHITELIST;
    }

    public ServerListSelection getModifyServerList() {
        return this.modifyServerList;
    }

    public enum ServerListSelection {
        TRUE,
        WHITELIST,
        FALSE
    }
}
