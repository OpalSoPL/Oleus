/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.services.impl.configurate.ConfigurateHelper;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@ImplementedBy(ConfigurateHelper.class)
public interface IConfigurateHelper {

    ConfigurationOptions setOptions(ConfigurationOptions options);

    default CommentedConfigurationNode createNode() {
        return CommentedConfigurationNode.root(this.setOptions(ConfigurationOptions.defaults()));
    }

    default ConfigurationOptions getOptions() {
        return this.setOptions(ConfigurationOptions.defaults());
    }

    void addTypeSerialiser(@Nullable TypeSerializerCollection collection);

    TypeSerializerCollection complete();
}
