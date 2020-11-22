/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.interfaces.INucleusTextTemplateFactory;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class NucleusTextTemplateTypeSerialiser implements TypeSerializer<NucleusTextTemplate> {

    private final INucleusTextTemplateFactory factory;

    public NucleusTextTemplateTypeSerialiser(final INucleusTextTemplateFactory factory) {
        this.factory = factory;
    }

    @Override
    public NucleusTextTemplateImpl deserialize(final Type type, final ConfigurationNode value) throws SerializationException {
        try {
            return this.factory.createFromAmpersandString(value.getString());
        } catch (final Throwable throwable) {
            throw new SerializationException(throwable);
        }
    }

    @Override
    public void serialize(final Type type, final NucleusTextTemplate obj, final ConfigurationNode value) throws SerializationException {
        if (obj != null) {
            value.set(LegacyComponentSerializer.legacyAmpersand().serialize(obj.asComponent()));
        }
    }
}
