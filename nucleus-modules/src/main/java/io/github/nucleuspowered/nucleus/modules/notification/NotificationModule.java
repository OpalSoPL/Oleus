/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.notification.command.BasicActionbarCommand;
import io.github.nucleuspowered.nucleus.modules.notification.command.BasicSubtitleCommand;
import io.github.nucleuspowered.nucleus.modules.notification.command.BasicTitleCommand;
import io.github.nucleuspowered.nucleus.modules.notification.command.BroadcastCommand;
import io.github.nucleuspowered.nucleus.modules.notification.command.PlainBroadcastCommand;
import io.github.nucleuspowered.nucleus.modules.notification.config.NotificationConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class NotificationModule implements IModule.Configurable<NotificationConfig> {

    public static final String ID = "notification";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                BasicActionbarCommand.class,
                BasicSubtitleCommand.class,
                BasicTitleCommand.class,
                BroadcastCommand.class,
                PlainBroadcastCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(NotificationPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Class<NotificationConfig> getConfigClass() {
        return NotificationConfig.class;
    }
}
