/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.teleport.config.TeleportConfig;
import io.github.nucleuspowered.nucleus.modules.teleport.runnables.TeleportAsyncTask;
import io.github.nucleuspowered.nucleus.modules.teleport.services.PlayerTeleporterService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class TeleportModule implements IModule.Configurable<TeleportConfig> {

    public static final String ID = "teleport";

    @Override public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(PlayerTeleporterService.class, new PlayerTeleporterService(serviceCollection), false);
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(TeleportPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.singleton(TeleportAsyncTask.class);
    }

    @Override public Class<TeleportConfig> getConfigClass() {
        return TeleportConfig.class;
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(TeleportKeys.TELEPORT_TOGGLE);
    }
}
