/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core;

import io.github.nucleuspowered.nucleus.core.commands.CommandInfoCommand;
import io.github.nucleuspowered.nucleus.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.core.commands.NucleusUserPrefsCommand;
import io.github.nucleuspowered.nucleus.core.commands.SetNucleusLanguageCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.ClearCacheCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.CompatibilityCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.DebugCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.DocGenCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.GetUserCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.InfoCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.MessagesUpdateCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.PrintPermsCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.RebuildUserCacheCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.ReloadCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.ResetUserCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.SaveCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.debug.GetUUIDSCommand;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.debug.RefreshUniqueVisitors;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.debug.VerifyCommandDescriptionsCommand;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.core.listeners.ChatChannelListener;
import io.github.nucleuspowered.nucleus.core.listeners.CoreListener;
import io.github.nucleuspowered.nucleus.core.listeners.WarmupListener;
import io.github.nucleuspowered.nucleus.core.runnables.CoreTask;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CoreModule implements IModule.Configurable<CoreConfig> {

    public static final String ID = "core";

    @Override
    public void init(INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                GetUUIDSCommand.class,
                RefreshUniqueVisitors.class,
                VerifyCommandDescriptionsCommand.class,
                ClearCacheCommand.class,
                CompatibilityCommand.class,
                DebugCommand.class,
                DocGenCommand.class,
                GetUserCommand.class,
                InfoCommand.class,
                MessagesUpdateCommand.class,
                PrintPermsCommand.class,
                RebuildUserCacheCommand.class,
                ReloadCommand.class,
                ResetUserCommand.class,
                SaveCommand.class,
                CommandInfoCommand.class,
                NucleusCommand.class,
                NucleusUserPrefsCommand.class,
                SetNucleusLanguageCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(CorePermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Arrays.asList(
                ChatChannelListener.class,
                CoreListener.class,
                WarmupListener.class
        );
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return Collections.singletonList(CoreTask.class);
    }

    @Override
    public Class<CoreConfig> getConfigClass() {
        return CoreConfig.class;
    }



}
