/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
public class ClientMessageReciever implements MessageReceiver {

    private final Logger logger;

    public ClientMessageReciever(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void sendMessage(final Text message) {
        this.logger.info(message.toPlain());
    }

    @Override
    public MessageChannel getMessageChannel() {
        return MessageChannel.TO_ALL;
    }

    @Override
    public void setMessageChannel(final MessageChannel channel) {
        // noop
    }
}
