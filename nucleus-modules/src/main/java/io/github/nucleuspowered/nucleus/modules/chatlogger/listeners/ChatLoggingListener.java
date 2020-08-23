/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.listeners;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.util.Optional;

import com.google.inject.Inject;

public class ChatLoggingListener extends AbstractLoggerListener {

    @Inject
    ChatLoggingListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection);
    }

    @Listener(order = Order.LAST)
    public void onCommand(final MessageChannelEvent.Chat event) {
        Util.onSourceSimulatedOr(event, this::getSource, this::onCommand);
    }

    private void onCommand(final MessageChannelEvent.Chat event, final CommandSource source) {
        log(event.getMessage().toPlain(), source);
    }

    @Listener(order = Order.LAST)
    public void onCommand(final SendCommandEvent event) {
        if (event.getCommand().equalsIgnoreCase("say") || event.getCommand().equalsIgnoreCase("minecraft:say")) {
            Util.onSourceSimulatedOr(event, this::getSource, this::onCommand);
        }
    }

    private void onCommand(final SendCommandEvent event, final CommandSource source) {
        log(event.getArguments(), source);
    }

    private void log(final String s, final CommandSource source) {
        final String message = this.messageProviderService.getMessageString("chatlog.chat", source.getName(), s);
        this.handler.queueEntry(message);
    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        final ChatLoggingConfig config = getConfig(serviceCollection);
        return config.isEnableLog() && config.isLogChat();
    }

    private Optional<CommandSource> getSource(final Event event) {
        return event.getCause().first(CommandSource.class);
    }

}
