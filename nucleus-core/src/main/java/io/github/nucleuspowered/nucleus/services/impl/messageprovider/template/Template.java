package io.github.nucleuspowered.nucleus.services.impl.messageprovider.template;

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Template {

    private final List<TextElement> elements;

    public Template(final List<TextElement> elements) {
        this.elements = elements;
    }

    public TextComponent create() {
        return this.create(Collections.emptyMap());
    }

    public TextComponent create(final Map<String, Component> replacements) {
        final TextComponent.Builder builder = TextComponent.builder();
        for (final TextElement element : this.elements) {
            builder.append(element.retrieve(replacements));
        }
        return builder.build();
    }

}
