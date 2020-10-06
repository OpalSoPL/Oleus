/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.io.IOException;
import java.util.Collection;
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
     * @param transformationCollection The {@link ConfigurationTransformation}s to apply.
     * @param <T> The type of config
     */
    <T> void registerModuleConfig(String moduleId, Class<T> typeOfConfig,
            Collection<ConfigurationTransformation<CommentedConfigurationNode>> transformationCollection);

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

    void prepareCoreConfig(Collection<ConfigurationTransformation<CommentedConfigurationNode>> coreConfigurationTransformations) throws IOException, ObjectMappingException;

    void prepareModuleConfig() throws IOException, ObjectMappingException;

    String getCoreConfigFileName();

    String getModuleConfigFileName();

    void reload();
}
