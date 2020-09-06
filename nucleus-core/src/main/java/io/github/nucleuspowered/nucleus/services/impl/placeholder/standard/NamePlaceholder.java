/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.placeholder.standard;

import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.util.Nameable;

import java.util.Optional;
import java.util.function.BiFunction;

public class NamePlaceholder<T> implements PlaceholderParser {

    private static final TextComponent CONSOLE = TextComponent.of("-");
    private final IPlayerDisplayNameService playerDisplayNameService;
    private final boolean consoleFilter;
    private final BiFunction<IPlayerDisplayNameService, T, Component> parser;
    private final ResourceKey resourceKey;
    private final Class<T> clazz;

    public NamePlaceholder(
            final Class<T> clazz,
            final IPlayerDisplayNameService playerDisplayNameService,
            final BiFunction<IPlayerDisplayNameService, T, Component> parser,
            final String id) {
        this(clazz, playerDisplayNameService, parser, id, false);
    }

    public NamePlaceholder(
            final Class<T> clazz,
            final IPlayerDisplayNameService playerDisplayNameService,
            final BiFunction<IPlayerDisplayNameService, T, Component> parser,
            final String id,
            final boolean consoleFilter) {
        this.clazz = clazz;
        this.playerDisplayNameService = playerDisplayNameService;
        this.parser = parser;
        this.consoleFilter = consoleFilter;
        this.resourceKey = ResourceKey.resolve(id);
    }

    @Override
    public Component parse(final PlaceholderContext placeholder) {
        final Optional<Object> associated = placeholder.getAssociatedObject();
        if (associated.isPresent()) {
            if (this.consoleFilter && associated.get() instanceof SystemSubject || associated.get() instanceof Server) {
                return CONSOLE;
            } else if (this.clazz.isInstance(associated.get())) {
                return this.parser.apply(this.playerDisplayNameService, this.clazz.cast(associated.get()));
            }
        }
        return TextComponent.empty();
    }

    @Override
    public ResourceKey getKey() {
        return this.resourceKey;
    }
}
