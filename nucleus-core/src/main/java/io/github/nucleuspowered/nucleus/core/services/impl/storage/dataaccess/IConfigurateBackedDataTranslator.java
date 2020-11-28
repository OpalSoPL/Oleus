/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.storage.dataaccess;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.configurate.IConfigurateBackedDataObject;
import io.github.nucleuspowered.storage.dataaccess.IDataTranslator;
import io.github.nucleuspowered.storage.exceptions.DataLoadException;
import io.github.nucleuspowered.storage.exceptions.DataSaveException;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public interface IConfigurateBackedDataTranslator<R extends IConfigurateBackedDataObject> extends IDataTranslator<R, JsonObject> {

    Gson GSON = new Gson();
    JsonParser JSON_PARSER = new JsonParser();

    @Override
    default R fromDataAccessObject(final JsonObject object) throws DataLoadException {
        final String json = IConfigurateBackedDataTranslator.GSON.toJson(object);
        final R obj = this.createNew();
        try {
            final ConfigurationNode node = GsonConfigurationLoader.builder()
                    .source(() -> new BufferedReader(new StringReader(json)))
                    .sink(() -> null) // we're not saving
                    .defaultOptions(this.getOptions())
                    .build()
                    .load();
            obj.setBackingNode(node);
        } catch (final ConfigurateException e) {
            throw new DataLoadException("Could not translate Json", e);
        }
        return obj;
    }

    @Override
    default JsonObject toDataAccessObject(final R object) throws DataSaveException {
        final ConfigurationNode node = object.getBackingNode();
        try (final StringWriter sw = new StringWriter();
             final BufferedWriter bw = new BufferedWriter(sw)) {
            GsonConfigurationLoader.builder()
                    .source(() -> null) // we're not loading
                    .sink(() -> bw) // we're saving
                    .defaultOptions(this.getOptions())
                    .build()
                    .save(node);
            return IConfigurateBackedDataTranslator.JSON_PARSER.parse(sw.toString()).getAsJsonObject();
        } catch (final IOException e) {
            throw new DataSaveException("Could not translate Json", e);
        }
    }

    ConfigurationOptions getOptions();

    ConfigurationNode createNewNode();

}
