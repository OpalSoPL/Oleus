/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.protection;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.protection.config.ProtectionConfig;
import io.github.nucleuspowered.nucleus.modules.protection.listeners.CropTrampleListener;
import io.github.nucleuspowered.nucleus.modules.protection.listeners.MobProtectionListener;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class ProtectionModule implements IModule.Configurable<ProtectionConfig> {

    public static final String ID = "protection";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Arrays.asList(
                CropTrampleListener.class,
                MobProtectionListener.class
        );
    }

    @Override
    public Class<ProtectionConfig> getConfigClass() {
        return ProtectionConfig.class;
    }

}
