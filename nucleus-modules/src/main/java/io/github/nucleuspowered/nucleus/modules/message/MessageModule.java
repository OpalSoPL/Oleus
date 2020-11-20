/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.message.commands.HelpOpCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.MessageCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.MsgToggleCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.ReplyCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.SocialSpyCommand;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfig;
import io.github.nucleuspowered.nucleus.modules.message.listener.MessageListener;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class MessageModule implements IModule.Configurable<MessageConfig> {

    public static final String ID = "message";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                HelpOpCommand.class,
                MessageCommand.class,
                MsgToggleCommand.class,
                ReplyCommand.class,
                SocialSpyCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(MessagePermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(MessageListener.class);
    }

    @Override
    public Class<MessageConfig> getConfigClass() {
        return MessageConfig.class;
    }

    @Listener
    public void onPreferenceKeyRegistration(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(MessageKeys.MESSAGE_TOGGLE);
        event.register(MessageKeys.SOCIAL_SPY);
    }

}
