/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class EnvironmentConfig {

    @Setting("maximum-weather-timespan")
    @LocalisedComment("config.environment.maxweathertime")
    private long maximumWeatherTimespan = -1;

    public long getMaximumWeatherTimespan() {
        return this.maximumWeatherTimespan;
    }
}
