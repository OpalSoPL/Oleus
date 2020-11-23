/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.configurate;

import io.github.nucleuspowered.storage.dataobjects.IDataObject;
import org.spongepowered.configurate.ConfigurationNode;

public interface IConfigurateBackedDataObject extends IDataObject {

    ConfigurationNode getBackingNode();

    void setBackingNode(ConfigurationNode node);
}
