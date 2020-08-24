/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class WarmupConfig {

    @Setting(value = "cancel-on-move", comment = "config.core.warmup.move")
    private final boolean onMove = true;

    @Setting(value = "cancel-on-command", comment = "config.core.warmup.command")
    private final boolean onCommand = true;

    public boolean isOnMove() {
        return this.onMove;
    }

    public boolean isOnCommand() {
        return this.onCommand;
    }
}
