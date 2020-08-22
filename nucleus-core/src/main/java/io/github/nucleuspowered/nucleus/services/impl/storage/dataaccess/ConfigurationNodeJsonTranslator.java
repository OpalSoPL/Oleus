/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.storage.dataaccess;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;

import java.util.List;
import java.util.Map;

public class ConfigurationNodeJsonTranslator {

    public static ConfigurationNodeJsonTranslator INSTANCE = new ConfigurationNodeJsonTranslator();

    private ConfigurationNodeJsonTranslator() {}

    public ConfigurationNode from(final ConfigurationNode nodeToPopulate, final JsonObject object) {
        this.parseObject(object, nodeToPopulate);
        return nodeToPopulate;
    }

    // The node has map
    public JsonObject jsonFrom(final ConfigurationNode node) {
        final JsonObject object = new JsonObject();
        if (!node.hasMapChildren()) {
            // nope.
            return object;
        }

        for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.getChildrenMap().entrySet()) {
            final ConfigurationNode value = entry.getValue();
            if (value.isVirtual()) {
                continue;
            }

            final String key = String.valueOf(entry.getKey());
            final JsonElement element = this.fromNode(value);

            if (element != null) {
                object.add(key, element);
            }
        }

        return object;
    }

    private JsonElement fromNode(final ConfigurationNode value) {
        JsonElement element = null;
        if (value.getValueType() == ValueType.MAP) {
            element = this.jsonFrom(value);
        } else if (value.getValueType() == ValueType.LIST) {
            element = this.jsonFromList(value.getChildrenList());
        } else if (value.getValueType() == ValueType.SCALAR) {
            element = this.jsonFromScalar(value.getValue());
        }

        return element;
    }

    private JsonPrimitive jsonFromScalar(final Object value) {
        final JsonPrimitive primitive;
        if (value instanceof Number) {
            primitive = new JsonPrimitive((Number) value);
        } else if (value instanceof Boolean) {
            primitive = new JsonPrimitive((Boolean) value);
        } else {
            primitive = new JsonPrimitive(value.toString());
        }

        return primitive;
    }

    private JsonArray jsonFromList(final List<? extends ConfigurationNode> listNode) {
        return listNode.stream().map(this::fromNode).collect(
                JsonArray::new,
                JsonArray::add,
                JsonArray::addAll
        );
    }

    private void parseArray(final JsonArray array, final ConfigurationNode node) {
        for (final JsonElement element : array) {
            if (!element.isJsonNull()) {
                this.parseElement(element, node.getAppendedNode());
            }
        }
    }

    private void parseObject(final JsonObject object, final ConfigurationNode node) {
        for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
            final JsonElement element = entry.getValue();
            this.parseElement(element, node.getNode(entry.getKey()));
        }
    }

    private void parsePrimitive(final JsonPrimitive primitive, final ConfigurationNode node) {
        if (primitive.isBoolean()) {
            node.setValue(primitive.getAsBoolean());
        } else if (primitive.isString()) {
            node.setValue(primitive.getAsString());
        } else if (primitive.isNumber()) {
            final double d = primitive.getAsDouble();
            final long l = primitive.getAsLong();
            if (d == l) {
                final int i = primitive.getAsInt();
                if (i == l) {
                    node.setValue(i);
                } else {
                    node.setValue(l);
                }
            } else {
                node.setValue(d);
            }
        }
    }

    private void parseElement(final JsonElement element, final ConfigurationNode node) {
        if (element.isJsonObject()) {
            this.from(node, element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            this.parseArray(element.getAsJsonArray(), node);
        } else if (element.isJsonPrimitive()) {
            this.parsePrimitive(element.getAsJsonPrimitive(), node);
        }
    }

}
