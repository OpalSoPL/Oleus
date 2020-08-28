/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.configurate.ConfigurateHelper;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@ImplementedBy(ConfigurateHelper.class)
public interface IConfigurateHelper {

    ConfigurationOptions setOptions(ConfigurationOptions options);

    default CommentedConfigurationNode createNode() {
        return CommentedConfigurationNode.root(this.setOptions(ConfigurationOptions.defaults()));
    }

    default ConfigurationOptions getOptions() {
        return this.setOptions(ConfigurationOptions.defaults());
    }
}
