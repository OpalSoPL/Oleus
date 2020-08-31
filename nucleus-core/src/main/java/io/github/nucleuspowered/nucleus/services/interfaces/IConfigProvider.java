/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.util.Map;

/**
 * Provides config objects.
 */
public interface IConfigProvider {

    CoreConfig getCoreConfig();

    /**
     * Registers how to obtain a config object.
     *
     * @param moduleId The module ID of the config.
     * @param typeOfConfig The type of configuration object.
     * @param <T> The type of config
     */
    <T> void registerModuleConfig(String moduleId, Class<T> typeOfConfig);

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

    void mergeCoreDefaults() throws IOException, ObjectMappingException;

    void mergeModuleDefaults() throws IOException, ObjectMappingException;

    String getCoreConfigFileName();

    String getModuleConfigFileName();

    void reload();
}
