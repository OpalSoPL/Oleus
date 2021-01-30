/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.data;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.mail.data.MailMessage;
import io.github.nucleuspowered.nucleus.core.util.TypeTokens;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

public class MailMessageSerialiser implements TypeSerializer<MailMessage> {

    @Override
    public MailMessage deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        try {
            return new MailData(
                    node.node("uuid").get(TypeTokens.UUID),
                    Instant.ofEpochMilli(node.node("date").getLong()),
                    node.node("message").getString()
            );
        } catch (final IllegalArgumentException e) {
            throw new SerializationException(type, "Could not create a mail message.", e);
        }
    }

    @Override
    public void serialize(final Type type, @Nullable final MailMessage obj, final ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.node("uuid").set(obj.getSender().orElse(Util.CONSOLE_FAKE_UUID));
            node.node("date").set(obj.getDate().toEpochMilli());
            node.node("message").set(obj.getMessage());
        }
    }
}
