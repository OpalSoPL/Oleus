/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.listeners;

import io.github.nucleuspowered.nucleus.api.module.message.event.NucleusMessageEvent;
import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

public class MessageLoggingListener extends AbstractLoggerListener {

    MessageLoggingListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection);
    }

    @Listener(order = Order.LAST)
    public void onCommand(final NucleusMessageEvent event) {
        final String sender =
                event.getSender().map(this.displayNameService::getName).map(PlainComponentSerializer.plain()::serialize).orElse("unknown");
        final String receiver =
                event.getRecipient().map(this.displayNameService::getName).map(PlainComponentSerializer.plain()::serialize).orElse("unknown");
        final String message = this.messageProviderService.getMessageString("chatlog.message", sender, receiver, event.getMessage());
        this.handler.queueEntry(message);
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        final ChatLoggingConfig config = this.getConfig(serviceCollection);
        return config.isEnableLog() && config.isLogMessages();
    }

}
