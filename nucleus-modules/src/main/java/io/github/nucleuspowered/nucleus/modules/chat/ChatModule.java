/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.chat.commands.MeCommand;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfig;
import io.github.nucleuspowered.nucleus.modules.chat.listeners.ChatListener;
import io.github.nucleuspowered.nucleus.modules.chat.services.ChatService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

// TODO: Add template suppliers to IPlayerDisplayNameService
public class ChatModule implements IModule.Configurable<ChatConfig> {

    public final static String ID = "chat";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(ChatService.class, new ChatService(), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singleton(MeCommand.class);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(ChatPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(ChatListener.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<ChatConfig> getConfigClass() {
        return ChatConfig.class;
    }

}
