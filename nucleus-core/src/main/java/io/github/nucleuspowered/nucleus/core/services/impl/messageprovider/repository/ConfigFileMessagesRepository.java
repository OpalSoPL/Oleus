/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.repository;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.ITextStyleService;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigFileMessagesRepository extends AbstractMessageRepository implements IMessageRepository {

    private static final Pattern KEYS = Pattern.compile("\\{(\\d+)}");

    private boolean isFailed = false;
    private final Path file;
    private final Logger logger;
    private final Supplier<PropertiesMessageRepository> messageRepositorySupplier;
    private CommentedConfigurationNode node = CommentedConfigurationNode.root();

    public ConfigFileMessagesRepository(
            final ITextStyleService textStyleService,
            final IPlayerDisplayNameService playerDisplayNameService,
            final Logger logger,
            final Path file,
            final Supplier<PropertiesMessageRepository> messageRepositorySupplier) {
        super(textStyleService, playerDisplayNameService);
        this.file = file;
        this.messageRepositorySupplier = messageRepositorySupplier;
        this.logger = logger;
    }

    public boolean isFailed() {
        return this.isFailed;
    }

    @Override
    public void invalidateIfNecessary() {
        this.invalidateIfNecessary(false);
    }

    public void invalidateIfNecessary(final boolean firstLoad) {
        this.cachedMessages.clear();
        this.cachedStringMessages.clear();
        this.load(firstLoad);
    }

    @Override public boolean hasEntry(final String key) {
        return false;
    }

    @Override String getEntry(final String key) {
        return null;
    }

    protected CommentedConfigurationNode getDefaults() {
        final CommentedConfigurationNode ccn = CommentedConfigurationNode.root();
        final PropertiesMessageRepository repository = this.messageRepositorySupplier.get();

        for (final String key : repository.getKeys()) {
            try {
                ccn.node((Object[]) key.split("\\.")).set(repository.getEntry(key));
            } catch (final ConfigurateException exception) {
                exception.printStackTrace();
            }
        }

        return ccn;
    }

    protected HoconConfigurationLoader getLoader(final Path file) {
        return HoconConfigurationLoader.builder().path(file).build();
    }

    public Optional<String> getKey(@NonNull final String key) {
        Preconditions.checkNotNull(key);
        final Object[] obj = key.split("\\.");
        return Optional.ofNullable(this.node.node(obj).getString());
    }

    public List<String> walkThroughForMismatched() {
        final Matcher keyMatcher = KEYS.matcher("");
        final List<String> keysToFix = new ArrayList<>();
        final PropertiesMessageRepository propertiesMessageRepository = this.messageRepositorySupplier.get();
        propertiesMessageRepository.getKeys().forEach(x -> {
            final String resKey = propertiesMessageRepository.getEntry(x);
            final Optional<String> msgKey = this.getKey(x);
            if (msgKey.isPresent() && this.getTokens(resKey, keyMatcher) != this.getTokens(msgKey.get(), keyMatcher)) {
                keysToFix.add(x);
            }
        });

        return keysToFix;
    }

    public void fixMismatched(final List<String> toFix) {
        Preconditions.checkNotNull(toFix);
        final PropertiesMessageRepository propertiesMessageRepository = this.messageRepositorySupplier.get();
        toFix.forEach(x -> {
            final String resKey = propertiesMessageRepository.getEntry(x);
            final Optional<String> msgKey = this.getKey(x);

            final Object[] nodeKey = x.split("\\.");
            try {
                final CommentedConfigurationNode cn = this.node.node(nodeKey).set(resKey);
                msgKey.ifPresent(cn::comment);
            } catch (final ConfigurateException ex) {
                ex.printStackTrace();
            }
        });

        this.save();
    }

    private int getTokens(final String message, final Matcher matcher) {
        int result = -1;

        matcher.reset(message);
        while (matcher.find()) {
            result = Math.max(result, Integer.parseInt(matcher.group(1)));
        }

        return result;
    }

    private void load(final boolean firstLoad) {
        try {
            this.node = this.getLoader(this.file).load();
            this.isFailed = false;
        } catch (final IOException e) {
            this.isFailed = true;
            final Throwable exception = e.getCause();
            this.logger.error("There might be an error in your messages file! The error is: ");
            this.logger.error(exception.getMessage());
            this.logger.error("If the error points to a line in the file, please correct this then run /nucleus reload");
            this.logger.error("Ignoring messages.conf for now.");
            exception.printStackTrace();
        }

        if (firstLoad) {
            // get defaults and merge in
            this.node.mergeFrom(this.getDefaults());
            this.fixMismatched(this.walkThroughForMismatched());
            this.save();
        }
    }

    private void save() {
        try {
            this.getLoader(this.file).save(this.node);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
