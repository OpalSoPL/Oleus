/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.interfaces.INucleusTextTemplateFactory;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class NucleusTextTemplateTypeSerialiser implements TypeSerializer<NucleusTextTemplateImpl> {

    private final INucleusTextTemplateFactory factory;

    public NucleusTextTemplateTypeSerialiser(final INucleusTextTemplateFactory factory) {
        this.factory = factory;
    }

    @Override public NucleusTextTemplateImpl deserialize(final TypeToken<?> type, final ConfigurationNode value) throws ObjectMappingException {
        try {
            return this.factory.createFromAmpersandString(value.getString());
        } catch (final Throwable throwable) {
            throw new ObjectMappingException(throwable);
        }
    }

    @Override public void serialize(final TypeToken<?> type, final NucleusTextTemplateImpl obj, final ConfigurationNode value) {
        value.setValue(obj.asComponent());
    }
}
