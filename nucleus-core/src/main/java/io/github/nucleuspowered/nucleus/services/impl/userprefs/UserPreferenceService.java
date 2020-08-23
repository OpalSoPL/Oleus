/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.userprefs;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import io.github.nucleuspowered.nucleus.util.LazyLoadFunction;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@Singleton
public class UserPreferenceService implements IUserPreferenceService {

    private final NucleusKeysProvider provider;

    private final Map<String, NucleusUserPreferenceService.PreferenceKey<?>> registered = new HashMap<>();
    private final Element element;

    public static final TextComponent PREFERENCE_ARG = Text.of("preference");
    public static final TextComponent VALUE_ARG = Text.of("value");
    private final INucleusServiceCollection serviceCollection;

    @Inject
    public UserPreferenceService(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        this.element = new Element(serviceCollection);
        this.provider = new NucleusKeysProvider(serviceCollection);
    }

    @Override
    public void postInit() {
        this.provider.getAll().forEach(x -> this.register((PreferenceKeyImpl<?>) x));
    }

    @Override public CommandElement getElement() {
        return this.element;
    }

    @Override public void register(final PreferenceKeyImpl<?> key) {
        if (this.registered.containsKey(key.getID())) {
            throw new IllegalArgumentException("ID already registered");
        }
        this.registered.put(key.getID(), key);
        this.element.keys.put(key.getID().toLowerCase().replaceAll("^nucleus:", ""), key);
        this.element.keys.put(key.getID().toLowerCase(), key);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T> void set(final UUID uuid, final NucleusUserPreferenceService.PreferenceKey<T> key, @Nullable final T value) {
        final PreferenceKeyImpl pki;
        if (Objects.requireNonNull(key) instanceof PreferenceKeyImpl) {
            pki = (PreferenceKeyImpl) key;
        } else {
            throw new IllegalArgumentException("Cannot have custom preference keys");
        }

        this.set(uuid, pki, value);
        ((PreferenceKeyImpl<T>) key).onSet(this.serviceCollection, uuid, value);
    }

    @Override public <T> void set(final UUID uuid, final PreferenceKeyImpl<T> key, @Nullable final T value) {
        this.serviceCollection
                .storageManager()
                .getUserService()
                .getOrNew(uuid)
                .thenAccept(x -> x.set(key, value));
    }

    @Override public Map<NucleusUserPreferenceService.PreferenceKey<?>, Object> get(final User user) {
        final Map<NucleusUserPreferenceService.PreferenceKey<?>, Object> ret = new HashMap<>();
        for (final NucleusUserPreferenceService.PreferenceKey<?> key : this.registered.values()) {
            if (((PreferenceKeyImpl) key).canAccess(this.serviceCollection, user)) {
                ret.put(key, this.get(user.getUniqueId(), key).orElse(null));
            }
        }

        return ret;
    }

    @Override public <T> Optional<T> get(final UUID uuid, final NucleusUserPreferenceService.PreferenceKey<T> key) {
        if (!this.registered.containsValue(key)) {
            throw new IllegalArgumentException("Key is not registered.");
        }

        if (!(key instanceof PreferenceKeyImpl)) {
            throw new IllegalArgumentException("Custom preference keys are not supported.");
        }

        final PreferenceKeyImpl<T> prefKey = (PreferenceKeyImpl<T>) key;
        Optional<T> ot = Optional.empty();
        try {
            ot = this.serviceCollection
                    .storageManager()
                    .getUserService()
                    .getOnThread(uuid)
                    .map(x -> x.getOrDefault(prefKey));
        } catch (final ClassCastException e) {
            e.printStackTrace();
        }

        return ot;
    }

    @Override public <T> T getUnwrapped(final UUID uuid, final NucleusUserPreferenceService.PreferenceKey<T> key) {
        return this.get(uuid, key).orElse(null);
    }

    @Override
    public NucleusKeysProvider keys() {
        return this.provider;
    }

    @Override
    public <T> Optional<T> getPreferenceFor(final User user, final NucleusUserPreferenceService.PreferenceKey<T> key) {
        return this.get(user.getUniqueId(), key);
    }

    @Override
    public <T> void setPreferenceFor(final User user, final NucleusUserPreferenceService.PreferenceKey<T> key, final T value) {
        this.set(user.getUniqueId(), key, value);
    }

    @Override
    public void removePreferenceFor(final User user, final NucleusUserPreferenceService.PreferenceKey<?> key) {
        this.set(user.getUniqueId(), key, null);
    }

    @Override public boolean canAccess(final User user, final PreferenceKey<?> key) {
        return ((PreferenceKeyImpl) key).canAccess(this.serviceCollection, user);
    }

    @Override public String getDescription(final PreferenceKey<?> key) {
        return ((PreferenceKeyImpl) key).getDescription(this.serviceCollection.messageProvider());
    }

    public static class Element extends CommandElement {

        private final INucleusServiceCollection serviceCollection;

        private enum Type {
            BOOLEAN(new LazyLoadFunction<>(s -> GenericArguments.bool(VALUE_ARG))),
            DOUBLE(new LazyLoadFunction<>(s -> GenericArguments.doubleNum(VALUE_ARG))),
            INTEGER(new LazyLoadFunction<>(s -> GenericArguments.integer(VALUE_ARG))),
            STRING(new LazyLoadFunction<>(s -> GenericArguments.remainingRawJoinedStrings(VALUE_ARG))),
            LOCALE(new LazyLoadFunction<>(s -> s.commandElementSupplier().createLocaleElement()));

            final LazyLoadFunction<INucleusServiceCollection, CommandElement> element;

            Type(final LazyLoadFunction<INucleusServiceCollection, CommandElement> element) {
                this.element = element;
            }
        }

        Element(final INucleusServiceCollection serviceCollection) {
            super(null);
            this.serviceCollection = serviceCollection;
        }

        private final Map<String, NucleusUserPreferenceService.PreferenceKey<?>> keys = new HashMap<>();

        @Nullable @Override public TextComponent getKey() {
            return Text.of("<preference> [value]");
        }

        @Override
        public void parse(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
            final String next = args.next().toLowerCase();
            final Type type = this.parseFirst(source, args, context, next);

            if (args.hasNext()) {
                type.element.apply(this.serviceCollection).parse(source, args, context);
            }
        }

        private Type parseFirst(final CommandSource source, final CommandArgs args, final CommandContext context, final String next) throws ArgumentParseException {
            final NucleusUserPreferenceService.PreferenceKey<?> key = this.keys.get(next);
            if (key != null) {
                Type type = null;
                final Class<?> cls = key.getValueClass();
                if (cls == boolean.class || cls == Boolean.class) {
                    type = Type.BOOLEAN;
                } else if (cls == int.class || cls == Integer.class) {
                    type = Type.INTEGER;
                } else if (cls == double.class || cls == Double.class) {
                    type = Type.DOUBLE;
                } else if (cls == String.class) {
                    type = Type.STRING;
                } else if (cls == Locale.class) {
                    type = Type.LOCALE;
                }

                if (type != null) {
                    this.checkAccess(key, this.getUser(source, args, context), args, source);
                    context.putArg(PREFERENCE_ARG, key);
                    return type;
                }
            }

            throw args.createError(this.serviceCollection.messageProvider().getMessageFor(source, "args.userprefs.incorrect", next));
        }

        private void checkAccess(final NucleusUserPreferenceService.PreferenceKey<?> key, final User user, final CommandArgs args, final CommandSource source)
                throws ArgumentParseException {
            if (!((PreferenceKeyImpl) key).canAccess(this.serviceCollection, user)) {
                if (source instanceof Player && ((Player) source).getUniqueId().equals(user.getUniqueId())) {
                    throw args.createError(this.serviceCollection.messageProvider().getMessageFor(source, "args.userprefs.noperm.self", key.getID()));
                }
                throw args.createError(this.serviceCollection.messageProvider().getMessageFor(source, "args.userprefs.noperm.other", user.getName(), key.getID()));
            }
        }

        private User getUser(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
            final Optional<User> o = context.getOne(NucleusParameters.Keys.USER);
            if (!o.isPresent()) {
                if (source instanceof User) {
                    return (User) source;
                }

                throw args.createError(this.serviceCollection.messageProvider().getMessageFor(source, "args.user.none"));
            } else {
                return o.get();
            }
        }

        @Nullable
        @Override
        protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
            return null;
        }

        @Override
        public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
            try {
                final User user = this.getUser(src, args, context);
                final CommandArgs.Snapshot snapshot = args.getSnapshot();
                final String arg1 = args.next().toLowerCase();

                if (!args.hasNext()) {
                    args.applySnapshot(snapshot);
                    // complete what we have.
                    return this.keys.entrySet().stream()
                            .filter(x -> x.getKey().startsWith(arg1))
                            .filter(x -> ((PreferenceKeyImpl) x.getValue()).canAccess(this.serviceCollection, user))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());
                } else {
                    return this.parseFirst(src, args, context, arg1).element.apply(this.serviceCollection).complete(src, args, context);
                }
            } catch (final ArgumentParseException e) {
                return ImmutableList.of();
            }
        }

        @Override
        public TextComponent getUsage(final CommandSource src) {
            return this.getKey();
        }
    }

    Map<String, NucleusUserPreferenceService.PreferenceKey<?>> getRegistered() {
        return this.registered;
    }

}
