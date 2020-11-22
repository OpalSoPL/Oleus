/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class VanishConfig {

    @Setting(value = "hide-connection-messages-on-vanish")
    @LocalisedComment("config.vanish.connectionmessages")
    private boolean suppressMessagesOnVanish = false;

    //@RequiresProperty("nucleus.vanish.tablist.enable")
    @Setting(value = "alter-tab-list")
    @LocalisedComment("config.vanish.altertablist")
    private boolean alterTabList = false;

    @Setting(value = "force-nucleus-vanish")
    @LocalisedComment("config.vanish.force")
    private boolean forceNucleusVanish = true;

    @Setting(value = "try-hide-players-in-seen")
    @LocalisedComment("config.vanish.hideseen")
    private boolean tryHidePlayers = true;

    public boolean isSuppressMessagesOnVanish() {
        return this.suppressMessagesOnVanish;
    }

    public boolean isAlterTabList() {
        return this.alterTabList;
    }

    public boolean isForceNucleusVanish() {
        return this.forceNucleusVanish;
    }

    public boolean isTryHidePlayers() {
        return this.tryHidePlayers;
    }
}
