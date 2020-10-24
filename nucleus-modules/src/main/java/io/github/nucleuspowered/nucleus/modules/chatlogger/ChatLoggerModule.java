/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfig;
import io.github.nucleuspowered.nucleus.modules.chatlogger.listeners.BaseLoggerListener;
import io.github.nucleuspowered.nucleus.modules.chatlogger.listeners.ChatLoggingListener;
import io.github.nucleuspowered.nucleus.modules.chatlogger.listeners.MailLoggingListener;
import io.github.nucleuspowered.nucleus.modules.chatlogger.listeners.MessageLoggingListener;
import io.github.nucleuspowered.nucleus.modules.chatlogger.runnables.ChatLoggerRunnable;
import io.github.nucleuspowered.nucleus.modules.chatlogger.services.ChatLoggerHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ChatLoggerModule implements IModule.Configurable<ChatLoggingConfig> {

    public static final String ID = "chat-logger";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(ChatLoggerHandler.class, new ChatLoggerHandler(serviceCollection), false);
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
                BaseLoggerListener.class,
                ChatLoggingListener.class,
                MailLoggingListener.class,
                MessageLoggingListener.class
        );
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.singleton(ChatLoggerRunnable.class);
    }

    @Override
    public Class<ChatLoggingConfig> getConfigClass() {
        return null;
    }
}
