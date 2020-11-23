/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class KitConfig {
    
    @Setting(value = "must-get-all-items")
    @LocalisedComment("config.kits.mustgetall")
    private boolean mustGetAll = false;
    
    @Setting(value = "drop-items-if-inventory-full")
    @LocalisedComment("config.kits.full")
    private boolean dropKitIfFull = false;

    
    @Setting(value = "process-tokens-in-lore")
    @LocalisedComment("config.kits.process-tokens")
    private boolean processTokens = false;

    @Setting(value = "auto-redeem")
    private AutoRedeem autoRedeem = new AutoRedeem();

    public boolean isMustGetAll() {
        return this.mustGetAll;
    }

    public boolean isDropKitIfFull() {
        return this.dropKitIfFull;
    }

    public boolean isProcessTokens() {
        return this.processTokens;
    }

    private AutoRedeem getAutoRedeem() {
        if (this.autoRedeem == null) {
            this.autoRedeem = new AutoRedeem();
        }

        return this.autoRedeem;
    }

    public boolean isEnableAutoredeem() {
        return this.getAutoRedeem().enableAutoRedeem;
    }

    public boolean isLogAutoredeem() {
        return this.getAutoRedeem().logAutoredeem;
    }

    @ConfigSerializable
    public static class AutoRedeem {
        
        @Setting(value = "log")
        @LocalisedComment("config.kits.logauto")
        private boolean logAutoredeem = false;

        
        @Setting(value = "enable")
        @LocalisedComment("config.kits.enableauto")
        private boolean enableAutoRedeem = false;
    }

}
