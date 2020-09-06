/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.placeholder;

import org.spongepowered.api.placeholder.PlaceholderParser;

public class PlaceholderMetadata {
    private final String token;
    private final PlaceholderParser parser;
    private final boolean document;

    PlaceholderMetadata(final String token, final PlaceholderParser parser, final boolean document) {
        this.token = token;
        this.parser = parser;
        this.document = document;
    }

    public String getToken() {
        return this.token;
    }

    public PlaceholderParser getParser() {
        return this.parser;
    }

    public boolean isDocument() {
        return this.document;
    }
}
