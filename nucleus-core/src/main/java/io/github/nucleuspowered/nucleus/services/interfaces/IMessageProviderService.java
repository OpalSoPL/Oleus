/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.MessageProviderService;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.ConfigFileMessagesRepository;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.IMessageRepository;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@ImplementedBy(MessageProviderService.class)
public interface IMessageProviderService {

    boolean hasKey(String key);

    Locale getDefaultLocale();

    void invalidateLocaleCacheFor(UUID uuid);

    Optional<Locale> getLocaleFromName(String name);

    Locale getLocaleFor(Audience commandSource);

    TextComponent getMessageFor(Locale locale, String key);

    TextComponent getMessageFor(Locale locale, String key, TextComponent... args);

    TextComponent getMessageFor(Locale locale, String key, Object... replacements);

    TextComponent getMessageFor(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, Object... replacements);

    default TextComponent getMessageForDefault(final String key, final Component... args) {
        return this.getMessageFor(this.getDefaultLocale(), key, args);
    }

    default TextComponent getMessageFor(final Audience source, final String key) {
        return this.getMessageFor(this.getLocaleFor(source), key);
    }

    default TextComponent getMessageFor(final Audience source, final String key, final Component... args) {
        return this.getMessageFor(this.getLocaleFor(source), key, args);
    }

    default TextComponent getMessageFor(final Audience source, final String key, final String... args) {
        final TextComponent[] t = Arrays.stream(args).map(LegacyComponentSerializer.legacyAmpersand()::deserialize).toArray(TextComponent[]::new);
        return this.getMessageFor(this.getLocaleFor(source), key, t);
    }

    default TextComponent getMessage(final String key) {
        return this.getMessageForDefault(key);
    }

    default TextComponent getMessage(final String key, final String... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default TextComponent getMessage(final String key, final Component... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default TextComponent getMessage(final String key, final Object... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default String getMessageString(final String key, final Object... replacements) {
        return this.getMessageString(this.getDefaultLocale(), key, replacements);
    }

    default String getMessageString(final Audience source, final String key, final String... replacements) {
        return this.getMessageString(this.getLocaleFor(source), key, replacements);
    }

    default TextComponent getMessageFor(final Audience source, final String key, final Object... replacements) {
        return this.getMessageFor(this.getLocaleFor(source), key, replacements);
    }

    default void sendMessageTo(final Audience receiver, final String key) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key));
    }

    default void sendMessageTo(final Audience receiver, final String key, final Object... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final Audience receiver, final String key, final String... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final Supplier<Audience> receiver, final String key, final String... replacements) {
        this.sendMessageTo(receiver.get(), key, replacements);
    }

    default void sendMessageTo(final Supplier<Audience> receiver, final String key, final Object... replacements) {
        this.sendMessageTo(receiver.get(), key, replacements);
    }

    boolean reloadMessageFile();

    IMessageRepository getMessagesRepository(Locale locale);

    ConfigFileMessagesRepository getConfigFileMessageRepository();

    default String getTimeToNow(final Locale locale, final Instant instant) {
        return this.getTimeString(locale, Instant.now().getEpochSecond() - instant.getEpochSecond());
    }

    String getTimeString(Locale locale, Duration duration);

    String getTimeString(Locale locale, long time);

    List<String> getAllLocaleNames();
}
