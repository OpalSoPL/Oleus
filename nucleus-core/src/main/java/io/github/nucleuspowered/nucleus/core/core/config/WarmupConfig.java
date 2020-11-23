/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class WarmupConfig {

    @Setting(value = "cancel-on-move")
    @LocalisedComment("config.core.warmup.move")
    private boolean onMove = true;

    @Setting(value = "cancel-on-command")
    @LocalisedComment("config.core.warmup.command")
    private boolean onCommand = true;

    public boolean isOnMove() {
        return this.onMove;
    }

    public boolean isOnCommand() {
        return this.onCommand;
    }
}
