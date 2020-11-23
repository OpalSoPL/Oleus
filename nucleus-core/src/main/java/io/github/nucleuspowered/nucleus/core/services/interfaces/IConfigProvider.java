/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.core.services.impl.configprovider.ConfigProvider;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provides config objects.
 */
@ImplementedBy(ConfigProvider.class)
public interface IConfigProvider {

    CoreConfig getCoreConfig();

    /**
     * Registers how to obtain a config object.
     *
     * @param moduleId The module ID of the config.
     * @param typeOfConfig The type of configuration object.
     * @param transformationCollection The {@link ConfigurationTransformation}s to apply.
     * @param <T> The type of config
     */
    <T> void registerModuleConfig(String moduleId,
            Class<T> typeOfConfig,
            Supplier<T> instanceCreator,
            Collection<ConfigurationTransformation> transformationCollection);

    /**
     * Gets the configuration of the given type.
     *
     * @param configType The type of configuration
     * @param <T> The type
     * @return The configuration
     * @throws IllegalArgumentException If the given type is invalid
     */
    <T> T getModuleConfig(Class<T> configType) throws IllegalArgumentException;

    Map<String, Class<?>> getModuleToConfigType();

    <T> T getDefaultModuleConfig(Class<T> configType) throws IllegalArgumentException;

    void prepareCoreConfig(Collection<ConfigurationTransformation> coreConfigurationTransformations) throws ConfigurateException;

    void prepareModuleConfig() throws ConfigurateException;

    String getCoreConfigFileName();

    String getModuleConfigFileName();

    void reload();
}
