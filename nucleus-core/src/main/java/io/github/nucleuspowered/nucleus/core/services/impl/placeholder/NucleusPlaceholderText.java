/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.placeholder;

import io.github.nucleuspowered.nucleus.core.util.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.placeholder.PlaceholderComponent;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;

import java.util.Collection;
import java.util.function.Function;

public class NucleusPlaceholderText implements PlaceholderComponent {

    private final PlaceholderContext context;
    private final PlaceholderParser parser;
    private final Collection<Function<Component, Component>> modifiers;

    public NucleusPlaceholderText(final PlaceholderContext context, final PlaceholderParser parser, final Collection<Function<Component, Component>> modifiers) {
        this.context = context;
        this.parser = parser;
        this.modifiers = modifiers;
    }

    @Override
    public PlaceholderContext getContext() {
        return this.context;
    }

    @Override
    public PlaceholderParser getParser() {
        return this.parser;
    }

    @Override
    public @NonNull Component asComponent() {
        Component result = this.parser.parse(this.context);
        if (!AdventureUtils.isEmpty(result)) {
            for (final Function<Component, Component> modifier : this.modifiers) {
                result = modifier.apply(result);
            }
        }
        return result;
    }
}
