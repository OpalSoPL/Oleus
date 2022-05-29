/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfig;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.util.Nameable;

public class ChatLoggingListener extends AbstractLoggerListener {

    @Inject
    public ChatLoggingListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection);
    }

    @Listener(order = Order.LAST)
    public void onChat(final PlayerChatEvent event) {
        this.log(PlainTextComponentSerializer.plainText().serialize(event.message()),
                event.cause().first(Nameable.class).map(Nameable::name).orElse("unknown"));
    }

    @Listener(order = Order.LAST)
    public void onCommand(final ExecuteCommandEvent.Post event) {
        if (event.command().equalsIgnoreCase("say") || event.command().equalsIgnoreCase("minecraft:say")) {
            if (event.cause().root() instanceof Nameable) {
                this.log(event.arguments(), ((Nameable) event.cause().root()).name());
            } else {
                this.log(event.arguments(), "Server");
            }
        }
    }

    private void log(final String s, final String name) {
        final String message = this.messageProviderService.getMessageString("chatlog.chat", name, s);
        this.handler.queueEntry(message);
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        final ChatLoggingConfig config = this.getConfig(serviceCollection);
        return config.isEnableLog() && config.isLogChat();
    }

}
