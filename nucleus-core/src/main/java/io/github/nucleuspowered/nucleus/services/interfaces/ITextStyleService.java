/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.textstyle.TextStyleService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.service.permission.Subject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Provides methods for resolving text colour and style permissions.
 * Will depend on the {@link IPermissionService}
 */
@ImplementedBy(TextStyleService.class)
public interface ITextStyleService {

    TextFormat EMPTY = new TextFormat() {
        @Override public Optional<TextColor> colour() {
            return Optional.empty();
        }

        @Override public Style style() {
            return Style.empty();
        }

        @Override public TextComponent textOf() {
            return TextComponent.empty();
        }

        @Override public TextComponent.Builder apply(final Component component) {
            return TextComponent.builder().append(component);
        }
    };

    List<String> getPermissionsFor(String prefix, Style colour);

    Style getResetStyle();

    Optional<String> getPermissionFor(String prefix, TextColor colour);

    List<String> getPermissionsFor(String prefix, TextDecoration style);

    /**
     * Removes formating codes based on permission.
     *
     * @param permissionPrefixColour The prefix of the permission to check for text colours
     * @param permissionPrefixStyle The prefix of the permission to check for text styles
     * @param text The text to strip
     * @return The text with the formatting stripped.
     */
    String stripPermissionless(String permissionPrefixColour, String permissionPrefixStyle, Subject source, String text);

    String stripPermissionless(String permissionPrefixColour, String permissionPrefixColor, String permissionPrefixStyle, Subject source,
            String oldMessage);

    Collection<String> wouldStrip(String permissionPrefixColour, String permissionPrefixStyle, Subject source, String text);

    default TextFormat getLastColourAndStyle(final Component text, @Nullable final TextFormat current) {
        return this.getLastColourAndStyle(text, current, null, Style.empty());
    }

    TextFormat getLastColourAndStyle(
            Component text,
            @Nullable TextFormat current,
            @Nullable TextColor defaultColour,
            Style defaultStyle);

    Optional<TextColor> getColourFromString(@Nullable String s);

    Style getTextStyleFromString(@Nullable String s);

    TextComponent addUrls(String message);

    TextComponent addUrls(String message, boolean replaceBlueUnderline);

    TextComponent getTextForUrl(String url, String msg, String whiteSpace, TextFormat st, @Nullable String optionString);

    TextComponent oldLegacy(String message);

    TextComponent joinTextsWithColoursFlowing(TextComponent... texts);

    interface TextFormat {

        Optional<TextColor> colour();

        Style style();

        TextComponent textOf();

        TextComponent.Builder apply(Component component);

    }

}
