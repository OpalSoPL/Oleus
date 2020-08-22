/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.MessageProviderService;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.ConfigFileMessagesRepository;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.IMessageRepository;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

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

    Locale getLocaleFor(CommandSource commandSource);

    Text getMessageFor(Locale locale, String key);

    Text getMessageFor(Locale locale, String key, Text... args);

    Text getMessageFor(Locale locale, String key, Object... replacements);

    Text getMessageFor(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, Object... replacements);

    default Text getMessageForDefault(final String key, final Text... args) {
        return this.getMessageFor(this.getDefaultLocale(), key, args);
    }

    default Text getMessageFor(final CommandSource source, final String key) {
        return this.getMessageFor(this.getLocaleFor(source), key);
    }

    default Text getMessageFor(final CommandSource source, final String key, final Text... args) {
        return this.getMessageFor(this.getLocaleFor(source), key, args);
    }

    default Text getMessageFor(final CommandSource source, final String key, final String... args) {
        final Text[] t = Arrays.stream(args).map(TextSerializers.FORMATTING_CODE::deserialize).toArray(Text[]::new);
        return this.getMessageFor(this.getLocaleFor(source), key, t);
    }

    default Text getMessage(final String key) {
        return this.getMessageForDefault(key);
    }

    default Text getMessage(final String key, final String... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default Text getMessage(final String key, final Text... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default Text getMessage(final String key, final Object... replacements) {
        return this.getMessageFor(this.getDefaultLocale(), key, replacements);
    }

    default String getMessageString(final String key, final Object... replacements) {
        return this.getMessageString(this.getDefaultLocale(), key, replacements);
    }

    default String getMessageString(final CommandSource source, final String key, final String... replacements) {
        return this.getMessageString(this.getLocaleFor(source), key, replacements);
    }

    default Text getMessageFor(final CommandSource source, final String key, final Object... replacements) {
        return this.getMessageFor(this.getLocaleFor(source), key, replacements);
    }

    default void sendMessageTo(final CommandSource receiver, final String key) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key));
    }

    default void sendMessageTo(final MessageReceiver receiver, final String key, final Object... replacements) {
        if (receiver instanceof CommandSource) {
            receiver.sendMessage(this.getMessageFor(this.getLocaleFor((CommandSource) receiver), key, replacements));
        } else {
            receiver.sendMessage(getMessageFor(Sponge.getServer().getConsole(), key, replacements));
        }
    }

    default void sendMessageTo(final CommandSource receiver, final String key, final Object... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final CommandSource receiver, final String key, final Text... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final CommandSource receiver, final String key, final String... replacements) {
        receiver.sendMessage(this.getMessageFor(this.getLocaleFor(receiver), key, replacements));
    }

    default void sendMessageTo(final Supplier<CommandSource> receiver, final String key, final String... replacements) {
        this.sendMessageTo(receiver.get(), key, replacements);
    }

    default void sendMessageTo(final Supplier<CommandSource> receiver, final String key, final Object... replacements) {
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
