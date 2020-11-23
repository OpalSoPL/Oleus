/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rules.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class RulesConfig {

    @Setting(value = "rules-title")
    @LocalisedComment("config.rules.title")
    private String rulesTitle = "&6Server Rules";

    public String getRulesTitle() {
        return this.rulesTitle;
    }
}
