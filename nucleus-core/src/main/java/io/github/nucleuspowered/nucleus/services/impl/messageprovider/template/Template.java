package io.github.nucleuspowered.nucleus.services.impl.messageprovider.template;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Template implements ComponentLike {

    private final List<TextElement> elements;
    private final Map<String, ArgumentElement> knownArgs;

    public Template(final List<TextElement> elements) {
        this.elements = elements;
        this.knownArgs = elements.stream()
                .filter(x -> x instanceof ArgumentElement)
                .map(x -> (ArgumentElement) x)
                .collect(Collectors.toMap(x -> x.key, x -> x));
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

    public Map<String, ArgumentElement> getKnownArgs() {
        return this.knownArgs;
    }

    public boolean hasTokens() {
        return !this.knownArgs.isEmpty();
    }

    @Override
    public @NonNull Component asComponent() {
        return this.create();
    }

}
