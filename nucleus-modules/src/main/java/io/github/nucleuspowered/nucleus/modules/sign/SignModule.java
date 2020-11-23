/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.sign;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.sign.listeners.SignListener;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class SignModule implements IModule {

    public static final String ID = "sign";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(SignPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(SignListener.class);
    }
}
