/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.listeners;

import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfig;
import io.github.nucleuspowered.nucleus.modules.chatlogger.services.ChatLoggerHandler;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;

abstract class AbstractLoggerListener implements ListenerBase.Conditional {

    final ChatLoggerHandler handler;
    final IMessageProviderService messageProviderService;
    final IPlayerDisplayNameService displayNameService;

    @Inject
    AbstractLoggerListener(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(ChatLoggerHandler.class);
        this.messageProviderService = serviceCollection.messageProvider();
        this.displayNameService = serviceCollection.playerDisplayNameService();
    }

    ChatLoggingConfig getConfig(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(ChatLoggingConfig.class);
    }

}
