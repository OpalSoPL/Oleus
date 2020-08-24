/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.config;

import io.github.nucleuspowered.neutrino.annotations.DoNotGenerate;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CoreConfig {

    @Setting(value = "debug-mode", comment = "config.debugmode")
    private final boolean debugmode = false;

    @Setting(value = "print-on-autosave", comment = "config.printonautosave")
    private final boolean printOnAutosave = false;

    @Setting(value = "use-custom-message-file", comment = "config.custommessages")
    private final boolean custommessages = false;

    @Setting(value = "warmup-canceling", comment = "config.core.warmup.info")
    private final WarmupConfig warmupConfig = new WarmupConfig();

    @Setting(value = "command-on-name-click", comment = "config.core.commandonname")
    private final String commandOnNameClick = "/msg {{subject}}";

    @Setting(value = "kick-on-stop")
    private final KickOnStopConfig kickOnStop = new KickOnStopConfig();

    @DoNotGenerate
    @Setting(value = "simulate-error-on-startup")
    private final boolean errorOnStartup = false;

    @Setting(value = "safe-teleport-check", comment = "config.core.safeteleport")
    private final SafeTeleportConfig safeTeleportConfig = new SafeTeleportConfig();

    @Setting(value = "console-overrides-exemptions", comment = "config.core.consoleoverrides")
    private final boolean consoleOverride = true;

    @Setting(value = "check-for-wildcard", comment = "config.core.wildcard")
    private final boolean checkForWildcard = true;

    @Setting(value = "show-warning-on-startup", comment = "config.core.warning-on-startup")
    private final boolean warningOnStartup = true;

    @Setting(value = "more-accurate-visitor-count", comment = "config.core.accurate")
    private final boolean moreAccurate = false;

    @Setting(value = "override-language", comment = "config.core.language")
    private final String serverLocale = "default";

    @Setting(value = "data-file-location", comment = "config.core.datafilelocation")
    private final String dataFileLocation = "default";

    @Setting(value = "offline-user-tab-limit", comment = "config.core.offlineusertablimit")
    private final int nicknameArgOfflineLimit = 20;

    @Setting(value = "enable-parent-perms", comment = "config.core.parentperms")
    private final boolean useParentPerms = true;

    @Setting(value = "enable-partial-name-matching", comment = "config.core.partialname")
    private final boolean partialMatch = true;

    @Setting(value = "use-client-locale-where-possible", comment = "config.core.clientlocale")
    private final boolean clientLocale = false;

    @Setting(value = "give-default-group-user-permissions", comment = "config.core.defaultperms")
    private final boolean giveDefaultsUserPermissions = false;

    public boolean isDebugmode() {
        return this.debugmode;
    }

    public boolean isPrintOnAutosave() {
        return this.printOnAutosave;
    }

    public boolean isCustommessages() {
        return this.custommessages;
    }

    public WarmupConfig getWarmupConfig() {
        return this.warmupConfig;
    }

    public String getCommandOnNameClick() {
        return this.commandOnNameClick;
    }

    public boolean isKickOnStop() {
        return this.kickOnStop.isKickOnStop();
    }

    public NucleusTextTemplateImpl getKickOnStopMessage() {
        return this.kickOnStop.getKickOnStopMessage();
    }

    public boolean isErrorOnStartup() {
        return this.errorOnStartup;
    }

    public SafeTeleportConfig getSafeTeleportConfig() {
        return this.safeTeleportConfig;
    }

    public boolean isConsoleOverride() {
        return this.consoleOverride;
    }

    public boolean isCheckForWildcard() {
        return this.checkForWildcard;
    }

    public boolean isWarningOnStartup() {
        return this.warningOnStartup;
    }

    public boolean isMoreAccurate() {
        return this.moreAccurate;
    }

    public String getServerLocale() {
        return this.serverLocale;
    }

    public int getNicknameArgOfflineLimit() {
        return this.nicknameArgOfflineLimit;
    }

    public boolean isUseParentPerms() {
        return this.useParentPerms;
    }

    public boolean isPartialMatch() {
        return this.partialMatch;
    }

    public boolean isClientLocaleWhenPossible() {
        return this.clientLocale;
    }

    public boolean isGiveDefaultsUserPermissions() {
        return this.giveDefaultsUserPermissions;
    }
}
