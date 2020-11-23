/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.regex.Pattern;

@ConfigSerializable
public class NicknameConfig {

    @Setting(value = "min-nickname-length")
    @LocalisedComment("config.nicknames.min")
    private int minNicknameLength = 3;

    @Setting(value = "max-nickname-length")
    @LocalisedComment("config.nicknames.max")
    private int maxNicknameLength = 20;

    @Setting(value = "prefix")
    @LocalisedComment("config.nicknames.prefix")
    private String prefix = "&b~";

    @Setting(value = "pattern")
    @LocalisedComment("config.nicknames.pattern")
    private Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+");

    public int getMinNicknameLength() {
        return this.minNicknameLength;
    }

    public int getMaxNicknameLength() {
        return this.maxNicknameLength;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Pattern getPattern() {
        return this.pattern;
    }
}
