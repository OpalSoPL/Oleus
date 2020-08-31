/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.configprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.guice.ConfigDirectory;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigurateHelper;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// TODO
@Singleton
public class ConfigProvider implements IConfigProvider {

    private static final String CORE_CONFIG_PATH = "core_config.conf";
    private static final String MODULES_CONFIG_PATH = "module_config.conf";

    private final Path configPath;
    private final Path modulesConfigPath;
    private final Path coreConfigPath;

    private final HoconConfigurationLoader coreLoader;
    private final HoconConfigurationLoader modulesLoader;
    private final Logger logger;

    @Nullable private CommentedConfigurationNode coreNode = null;
    @Nullable private CommentedConfigurationNode moduleNode = null;
    @Nullable private CoreConfig coreConfig = null;
    private final Map<String, Class<?>> moduleConfigs = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    private final Map<Class<?>, Object> cachedConfig = new HashMap<>();

    @Inject
    public ConfigProvider(@ConfigDirectory final Path configPath, final IConfigurateHelper configurateHelper, final Logger logger) {
        this.configPath = configPath;
        this.coreConfigPath = this.configPath.resolve(CORE_CONFIG_PATH);
        this.modulesConfigPath = this.configPath.resolve(MODULES_CONFIG_PATH);
        this.logger = logger;
        this.coreLoader = ConfigProvider.getLoader(configurateHelper, this.coreConfigPath);
        this.modulesLoader = ConfigProvider.getLoader(configurateHelper, this.modulesConfigPath);
    }

    @Override
    public CoreConfig getCoreConfig() {
        return null;
    }

    @Override public <T> void registerModuleConfig(final String moduleId,
            final Class<T> typeOfConfig) {
        if (this.providers.containsKey(typeOfConfig) || this.moduleConfigs.containsKey(moduleId)) {
            throw new IllegalStateException("Cannot register type or module more than once!");
        }

        this.moduleConfigs.put(moduleId, typeOfConfig);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getModuleConfig(final Class<T> configType) throws IllegalArgumentException {
        if (configType == CoreConfig.class) {
            return (T) this.getCoreConfig();
        }
        if (this.providers.containsKey(configType)) {
            return (T) this.providers.get(configType).get();
        }

        throw new IllegalArgumentException(configType.getSimpleName() + " does not exist");
    }

    @Override public Map<String, Class<?>> getModuleToConfigType() {
        return ImmutableMap.copyOf(this.moduleConfigs);
    }

    @Override public <T> T getDefaultModuleConfig(final Class<T> configType) throws IllegalArgumentException {
        if (this.providers.containsKey(configType)) {
            try {
                return configType.newInstance();
            } catch (final InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("Could not instantiate", e);
            }
        }

        throw new IllegalArgumentException(configType.getSimpleName() + " does not exist");
    }

    public void loadCore(final boolean actualLoad) throws IOException, ObjectMappingException {
        if (actualLoad) {
            this.coreNode = this.coreLoader.load();
        }
        this.coreConfig = this.coreNode.getValue(TypeToken.of(CoreConfig.class), (Supplier<CoreConfig>) CoreConfig::new);
    }

    public void loadModules(final boolean actualLoad) throws IOException {
        if (actualLoad) {
            this.moduleNode = this.modulesLoader.load();
        }
        this.cachedConfig.clear();
        for (final Map.Entry<String, Class<?>> moduleClass : this.moduleConfigs.entrySet()) {
            try {
                this.cachedConfig.put(moduleClass.getValue(), this.get(this.moduleNode.getNode(moduleClass.getKey()), moduleClass.getValue()));
            } catch (final ObjectMappingException exception) {
                this.logger.error("Could not load module config section for '" + moduleClass.getKey() + "' - using default.", exception);
                this.cachedConfig.put(moduleClass.getValue(), this.moduleNode.getNode(moduleClass.getKey()));
            }
        }
    }

    @Override public void mergeCoreDefaults() throws IOException, ObjectMappingException {
        final CommentedConfigurationNode coreToSave = this.coreLoader.load();
        coreToSave.mergeValuesFrom(this.coreLoader.createEmptyNode().setValue(TypeToken.of(CoreConfig.class), new CoreConfig()));
        this.coreLoader.save(coreToSave);
        this.coreNode = coreToSave;
        this.loadCore(false);
    }

    @Override public void mergeModuleDefaults() throws IOException, ObjectMappingException {
        final CommentedConfigurationNode modulesToSave = this.modulesLoader.load();
        final CommentedConfigurationNode defaults = this.modulesLoader.createEmptyNode();
        for (final Map.Entry<String, Class<?>> moduleClass : this.moduleConfigs.entrySet()) {
             this.set(defaults.getNode(moduleClass.getKey()), moduleClass.getValue());
        }
        modulesToSave.mergeValuesFrom(defaults);
        this.modulesLoader.save(modulesToSave);
        this.moduleNode = modulesToSave;
        this.loadModules(false);
    }

    @Override public String getCoreConfigFileName() {
        return CORE_CONFIG_PATH;
    }

    @Override public String getModuleConfigFileName() {
        return MODULES_CONFIG_PATH;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(final CommentedConfigurationNode node, final Class<T> clazz) throws ObjectMappingException {
        return node.getValue(TypeToken.of(clazz), (Supplier<T>) () -> (T) this.providers.get(clazz).get());
    }

    @SuppressWarnings("unchecked")
    private <T> void set(final CommentedConfigurationNode node, final Class<T> clazz) throws ObjectMappingException {
        node.setValue(TypeToken.of(clazz), (T) this.providers.get(clazz).get());
    }

    @Override
    public void reload() {
        try {
            this.loadCore(true);
        } catch (final IOException | ObjectMappingException e) {
            this.logger.error("Could not load core configuration file. Using default.", e);
        }

        try {
            this.loadModules(true);
        } catch (final IOException e) {
            this.logger.error("Could not load module configuration file. Using default.", e);
        }
    }

    private static HoconConfigurationLoader getLoader(final IConfigurateHelper helper, final Path path) {
        return HoconConfigurationLoader.builder()
                .setDefaultOptions(helper.getOptions())
                .setPath(path)
                .build();
    }
}
