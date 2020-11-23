/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.nickname.commands.DelNickCommand;
import io.github.nucleuspowered.nucleus.modules.nickname.commands.NicknameCommand;
import io.github.nucleuspowered.nucleus.modules.nickname.commands.RealnameCommand;
import io.github.nucleuspowered.nucleus.modules.nickname.config.NicknameConfig;
import io.github.nucleuspowered.nucleus.modules.nickname.listeners.NicknameListener;
import io.github.nucleuspowered.nucleus.modules.nickname.services.NicknameService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class NicknameModule implements IModule.Configurable<NicknameConfig> {

    public final static String ID = "nickname";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        final NicknameService nicknameService = new NicknameService(serviceCollection);
        serviceCollection.registerService(NicknameService.class, nicknameService, false);
    }

    @Override
    public void postLoad(final INucleusServiceCollection serviceCollection) {
        // Register resolver and query.
        serviceCollection.getServiceUnchecked(NicknameService.class).injectResolver(serviceCollection);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                DelNickCommand.class,
                NicknameCommand.class,
                RealnameCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(NicknamePermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(NicknameListener.class);
    }

    @Override
    public Class<NicknameConfig> getConfigClass() {
        return NicknameConfig.class;
    }
}
