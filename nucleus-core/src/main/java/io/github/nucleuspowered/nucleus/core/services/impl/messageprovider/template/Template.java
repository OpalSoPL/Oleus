/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.template;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Template implements ComponentLike {

    private final List<TextElement> elements;

    public Template(final List<TextElement> elements) {
        this.elements = elements;
    }

    public Component create() {
        return this.create(Collections.emptyMap());
    }

    public Component create(final Map<String, Component> replacements) {
        final TextComponent.Builder builder = Component.text();
        for (final TextElement element : this.elements) {
            builder.append(element.retrieve(replacements));
        }
        return builder.build();
    }

    @Override
    public @NonNull Component asComponent() {
        return this.create();
    }

}
