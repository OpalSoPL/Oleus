/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class TeleportConfig {

    @Setting(value = "use-safe-teleportation")
    @LocalisedComment("config.teleport.safe")
    private boolean useSafeTeleport = true;

    @Setting(value = "default-quiet")
    @LocalisedComment("config.teleport.quiet")
    private boolean defaultQuiet = true;

    @Setting(value = "refund-on-deny")
    @LocalisedComment("config.teleport.refundondeny")
    private boolean refundOnDeny = true;

    @Setting(value = "only-same-dimension")
    @LocalisedComment("config.teleport.onlySameDimension")
    private boolean onlySameDimension = false;

    @Setting(value = "start-cooldown-when-asking")
    @LocalisedComment("config.teleport.cooldownOnAsk")
    private boolean cooldownOnAsk = false;

    @Setting(value = "show-clickable-tpa-accept-deny")
    @LocalisedComment("config.teleport.clickableAcceptDeny")
    private boolean showClickableAcceptDeny = false;

    @Setting(value = "use-commands-when-clicking-tpa-accept-deny")
    @LocalisedComment("config.teleport.useCommandOnClick")
    private boolean useCommandOnClickAcceptOrDeny = false;

    @Setting(value = "use-request-location-on-tp-requests")
    @LocalisedComment("config.teleport.useRequestLocation")
    private boolean useRequestLocation = false;

    public boolean isDefaultQuiet() {
        return this.defaultQuiet;
    }

    public boolean isUseSafeTeleport() {
        return this.useSafeTeleport;
    }

    public boolean isRefundOnDeny() {
        return this.refundOnDeny;
    }

    public boolean isOnlySameDimension() {
        return this.onlySameDimension;
    }

    public boolean isCooldownOnAsk() {
        return this.cooldownOnAsk;
    }

    public boolean isUseCommandsOnClickAcceptOrDeny() {
        return this.useCommandOnClickAcceptOrDeny;
    }

    public boolean isShowClickableAcceptDeny() {
        return this.showClickableAcceptDeny;
    }

    public boolean isUseRequestLocation() {
        return this.useRequestLocation;
    }
}
