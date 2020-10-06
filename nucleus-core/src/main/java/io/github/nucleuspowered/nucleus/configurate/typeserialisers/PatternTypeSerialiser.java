package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public final class PatternTypeSerialiser implements TypeSerializer<Pattern> {

    @SuppressWarnings("ConstantConditions")
    @Override
    public Pattern deserialize(final Type type, final ConfigurationNode value) throws ObjectMappingException {
        if (value.isVirtual()) {
            return null;
        }
        return Pattern.compile(value.getString());
    }

    @Override
    public void serialize(final Type type, final Pattern obj, final ConfigurationNode value) throws ObjectMappingException {
        if (obj != null) {
            value.setValue(TypeToken.get(String.class), obj.pattern());
        }
    }
}
