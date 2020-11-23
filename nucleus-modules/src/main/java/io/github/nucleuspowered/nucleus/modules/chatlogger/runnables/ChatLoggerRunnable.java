/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.runnables;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfig;
import io.github.nucleuspowered.nucleus.modules.chatlogger.services.ChatLoggerHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

// TODO: Only run if enabled
public class ChatLoggerRunnable implements TaskBase, IReloadableService.Reloadable {

    private final ChatLoggerHandler handler;
    private ChatLoggingConfig config = new ChatLoggingConfig();

    @Inject
    public ChatLoggerRunnable(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(ChatLoggerHandler.class);
    }

    @Override
    public Duration interval() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            if (this.config.isEnableLog()) {
                this.handler.onTick();
            }
        } catch (final IllegalStateException e) {
            // noop
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.config = serviceCollection.configProvider().getModuleConfig(ChatLoggingConfig.class);
    }
}
