/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.configurate;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.CommentedConfigurationNodeIntermediary;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.objectmapping.meta.Processor;
import org.spongepowered.configurate.serialize.SerializationException;

public final class ObjectMapperActions {

    static Processor.Factory<LocalisedComment, Object> localisedComments(final IMessageProviderService source) {
        return (data, fieldType) -> {
            final String translated = source.getMessageString(data.value(), (Object[]) data.replacements());
            return (value, destination) -> {
                if (destination instanceof CommentedConfigurationNodeIntermediary<?>) {
                    final CommentedConfigurationNodeIntermediary<?> commented = (CommentedConfigurationNodeIntermediary<?>) destination;
                    commented.comment(translated);
                }
            };
        };
    }

}
