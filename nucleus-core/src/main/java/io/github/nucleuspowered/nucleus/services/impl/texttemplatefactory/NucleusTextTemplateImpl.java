/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory;

import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NucleusTextTemplateImpl implements NucleusTextTemplate {

    private final List<BiFunction<Object, Map<String, Function<Object, Optional<ComponentLike>>>, Component>> texts;
    @Nullable private final Component prefix;
    @Nullable private final Component suffix;
    private final INucleusServiceCollection serviceCollection;

    public NucleusTextTemplateImpl(
            final INucleusServiceCollection serviceCollection,
            final List<BiFunction<Object, Map<String, Function<Object, Optional<ComponentLike>>>, Component>> texts,
            @Nullable final Component prefix,
            @Nullable final Component suffix) {
        this.texts = texts;
        this.prefix = prefix;
        this.suffix = suffix;
        this.serviceCollection = serviceCollection;
    }

    public static NucleusTextTemplateImpl empty() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return this.texts.isEmpty();
    }

    @Override
    public Optional<Component> getPrefix() {
        return Optional.ofNullable(this.prefix);
    }

    @Override
    public Optional<Component> getSuffix() {
        return Optional.ofNullable(this.suffix);
    }

    @Override
    public boolean containsTokens() {
        return this.texts.stream().anyMatch(x -> x instanceof TemplateParser.PlaceholderElement);
    }

    @Override
    public Component getForObject(final Object source) {
        return this.getForObjectWithTokens(source, null);
    }

    @Override
    public Component getForObjectWithSenderToken(final Object source, final Object sender) {
        final Optional<ComponentLike> s =
                Optional.of(this.serviceCollection.placeholderService().parse(sender, "displayname"));
        return this.getForObjectWithSenderToken(source,
                ImmutableMap.<String, Function<Object, Optional<? extends ComponentLike>>>builder()
                        .put("sender", se -> s)
                        .build());
    }

    @Override
    public Component getForObjectWithTokens(final Object source, @Nullable final Map<String, Function<Object, Optional<ComponentLike>>> tokensArray) {
        final TextComponent.Builder builder = Component.text();
        if (this.prefix != null) {
            builder.append(this.prefix);
        }

        for (final BiFunction<Object, Map<String, Function<Object, Optional<ComponentLike>>>, Component> textComponent : this.texts) {
            builder.append(textComponent.apply(source, tokensArray));
        }

        if (this.suffix != null) {
            builder.append(this.suffix);
        }

        return builder.build();
    }

    @Override
    public @NonNull Component asComponent() {
        return this.getForObject(Sponge.getSystemSubject());
    }
}
