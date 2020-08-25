/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.userprefs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.entity.living.player.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

@Singleton
public class UserPreferenceService implements IUserPreferenceService {

    private final NucleusKeysProvider provider;

    private final Map<String, NucleusUserPreferenceService.PreferenceKey<?>> registered = new HashMap<>();
    private final INucleusServiceCollection serviceCollection;

    @Inject
    public UserPreferenceService(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        this.provider = new NucleusKeysProvider(serviceCollection);
    }

    @Override
    public void postInit() {
        this.provider.getAll().forEach(x -> this.register((PreferenceKeyImpl<?>) x));
    }

    @Override public void register(final PreferenceKeyImpl<?> key) {
        if (this.registered.containsKey(key.getID())) {
            throw new IllegalArgumentException("ID already registered");
        }
        this.registered.put(key.getID(), key);
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

    Map<String, NucleusUserPreferenceService.PreferenceKey<?>> getRegistered() {
        return this.registered;
    }

}
