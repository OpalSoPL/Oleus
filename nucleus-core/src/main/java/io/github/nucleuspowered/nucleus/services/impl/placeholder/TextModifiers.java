/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.placeholder;

import net.kyori.adventure.text.Component;

import java.util.function.Function;

public enum TextModifiers implements Function<Component, Component> {

    SPACE_AFTER("s") {
        @Override
        public Component apply(final Component text) {
            return Component.text().append(text).append(Component.space()).build();
        }
    },
    SPACE_BEFORE("p") {
        @Override
        public Component apply(final Component text) {
            return Component.text().append(Component.space()).append(text).build();
        }
    };

    private final String key;

    TextModifiers(final String p) {
        this.key = p;
    }

    public String getKey() {
        return this.key;
    }
}
