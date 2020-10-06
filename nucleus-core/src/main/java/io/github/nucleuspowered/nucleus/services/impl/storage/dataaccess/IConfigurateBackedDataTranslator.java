/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.storage.dataaccess;

import com.google.gson.JsonObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.configurate.IConfigurateBackedDataObject;
import io.github.nucleuspowered.storage.dataaccess.IDataTranslator;
import org.spongepowered.configurate.ConfigurationNode;

public interface IConfigurateBackedDataTranslator<R extends IConfigurateBackedDataObject> extends IDataTranslator<R, JsonObject> {

    @Override
    default R fromDataAccessObject(final JsonObject object) {
        // Get the ConfigNode from the JsonObject
        final ConfigurationNode node = ConfigurationNodeJsonTranslator.INSTANCE.from(this.createNewNode(), object);
        final R obj = this.createNew();
        obj.setBackingNode(node);
        return obj;
    }

    @Override
    default JsonObject toDataAccessObject(final R object) {
        final ConfigurationNode node = object.getBackingNode();
        return ConfigurationNodeJsonTranslator.INSTANCE.jsonFrom(node);
    }

    ConfigurationNode createNewNode();

}
