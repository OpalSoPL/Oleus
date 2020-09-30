/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfig;
import io.github.nucleuspowered.nucleus.modules.kit.storage.KitStorageModule;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.modules.kit.storage.IKitDataObject;

import java.util.Collection;
import java.util.Optional;

public class KitModule implements IModule.Configurable<KitConfig> {

    public static final String ID = "kit";

    @Inject
    public KitModule(final INucleusServiceCollection collection) {
        collection.storageManager().register(IKitDataObject.class, new KitStorageModule(collection));
    }

    @Override
    public Class<KitConfig> getConfigClass() {
        return KitConfig.class;
    }

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return null;
    }
}
