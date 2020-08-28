/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.configprovider;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.guice.ConfigDirectory;
import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.util.Tristate;
import uk.co.drnaylor.quickstart.ModuleHolder;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.inject.Singleton;

// TODO
@Singleton
public class ConfigProvider implements IConfigProvider, IReloadableService.Reloadable {

    private final Path configPath;

    @Inject
    public ConfigProvider(@ConfigDirectory final Path configPath) {
        this.configPath = configPath;
    }

    private final Map<String, Class<?>> moduleConfigs = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();

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

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {

    }
}
