/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.docgen;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class TokenDoc {

    @Setting("name")
    private String name;

    @Setting("id")
    private String externalId;

    @Setting("description")
    private String description;

    public String getName() {
        return this.name;
    }

    public TokenDoc setName(final String name) {
        this.name = name;
        return this;
    }

    public TokenDoc setId(final String id) {
        this.externalId = id;
        return this;
    }

    public TokenDoc setDescription(final String description) {
        this.description = description;
        return this;
    }
}