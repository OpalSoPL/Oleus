/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.data;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.mail.data.MailMessage;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.util.Identifiable;

import java.time.Instant;

public class MailMessageSerialiser implements TypeSerializer<MailMessage> {

    @Nullable
    @Override
    public MailMessage deserialize(@NonNull final TypeToken<?> type, @NonNull final ConfigurationNode value) throws ObjectMappingException {
        if (value.isVirtual()) {
            return null;
        }

        try {
            return new MailData(
                    value.getNode("uuid").getValue(TypeTokens.UUID),
                    Instant.ofEpochMilli(value.getNode("date").getLong()),
                    value.getNode("message").getString()
            );
        } catch (final IllegalArgumentException e) {
            throw new ObjectMappingException("Could not create a mail message.", e);
        }
    }

    @Override
    public void serialize(@NonNull final TypeToken<?> type, @Nullable final MailMessage obj, @NonNull final ConfigurationNode value) {
        if (obj != null) {
            value.getNode("uuid").setValue(obj.getSender().map(Identifiable::getUniqueId).orElse(Util.CONSOLE_FAKE_UUID));
            value.getNode("date").setValue(obj.getDate().toEpochMilli());
            value.getNode("message").setValue(obj.getMessage());
        }
    }
}
