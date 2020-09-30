/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.teleport.config.TeleportConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;

import java.util.Collection;
import java.util.Optional;

public final class TeleportModule implements IModule.Configurable<TeleportConfig> {

    public static final String ID = "teleport";

    @Override public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }

    @Override public Collection<Class<? extends TaskBase>> getTasks() {
        return null;
    }

    @Override public Class<TeleportConfig> getConfigClass() {
        return TeleportConfig.class;
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(
            new PreferenceKeyImpl.BooleanKey(
                NucleusKeysProvider.TELEPORT_TARGETABLE_KEY,
                true,
                TeleportPermissions.BASE_TPTOGGLE,
                "userpref.teleporttarget"
            )
        );
    }
}
