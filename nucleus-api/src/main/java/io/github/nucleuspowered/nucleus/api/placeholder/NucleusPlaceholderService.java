/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.placeholder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.placeholder.PlaceholderComponent;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.service.permission.Subject;

import java.util.Optional;

/**
 * Provides a way to determine how Nucleus uses placeholders.
 */
public interface NucleusPlaceholderService {

    /**
     * Parses the given input token in the context of the given sender.
     *
     * <p>This method will determine the placeholder to use from the
     * Nucleus perspective - so any Nucleus specific tokens can be parsed via
     * this method.</p>
     *
     * @param sender The sender to associate the placeholder output with.
     * @param input The token input
     * @return The {@link ComponentLike} if valid, or {@link Component#empty()}
     */
    ComponentLike parse(@Nullable Object sender, String input);

    /**
     * Gets the parser associated with the provided token name, if any,
     * prefixing un-namespaced tokens with the Nucleus prefix.
     *
     * @param token The token name
     * @return The {@link PlaceholderParser}, if any
     */
    Optional<PlaceholderParser> getParser(String token);

    /**
     * Gets the Nucleus placeholder parser for displaying
     * {@link Subject#option(String)}
     *
     * @return The parser.
     */
    PlaceholderParser optionParser();

    /**
     * Creates a {@link PlaceholderComponent} for displaying a {@link Subject}s
     * option.
     *
     * @param subject The {@link Subject}
     * @param option The option to display
     * @return The {@link PlaceholderComponent}
     */
    PlaceholderComponent textForSubjectAndOption(Subject subject, String option);

}
