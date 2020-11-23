/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class BlockSpawnsConfig {

    @Setting(value = "block-vanilla-mobs")
    @LocalisedComment("config.blockspawn.vanilla")
    private boolean blockVanillaMobs = false;

    @Setting(value = "block-mobs-with-ids")
    @LocalisedComment("config.blockspawn.ids")
    private List<String> idsToBlock = new ArrayList<>();

    public boolean isBlockVanillaMobs() {
        return this.blockVanillaMobs;
    }

    public List<String> getIdsToBlock() {
        return this.idsToBlock.stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
