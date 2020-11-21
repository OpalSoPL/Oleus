/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class BroadcastConfig {

    @Setting
    private String prefix = "&a[Broadcast] ";

    @Setting
    private String suffix = "";

    public String getPrefix() {
        return this.prefix == null ? "" : this.prefix;
    }

    public String getSuffix() {
        return this.suffix == null ? "" : this.suffix;
    }
}
