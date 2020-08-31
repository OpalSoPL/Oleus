/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.storage.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.nucleuspowered.storage.dataaccess.IDataTranslator;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;
import io.github.nucleuspowered.storage.dataobjects.keyed.IKeyedDataObject;
import io.github.nucleuspowered.storage.persistence.IStorageRepository;
import io.github.nucleuspowered.storage.queryobjects.IQueryObject;
import io.github.nucleuspowered.storage.util.KeyedObject;
import io.github.nucleuspowered.storage.util.ThrownBiConsumer;
import io.github.nucleuspowered.storage.util.ThrownFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.plugin.PluginContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

public abstract class AbstractKeyedService<Q extends IQueryObject<UUID, Q>, D extends IKeyedDataObject<D>>
        implements IStorageService.Keyed.KeyedData<UUID, Q, D> {

    private final LoadingCache<UUID, ReentrantReadWriteLock> dataLocks =
            Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<UUID, ReentrantReadWriteLock>() {
                @NonNull
                @Override
                public ReentrantReadWriteLock load(@Nonnull final UUID key) {
                    return new ReentrantReadWriteLock();
                }
            });
    private final Cache<UUID, D> cache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();
    private final Set<UUID> dirty = new HashSet<>();

    private final Supplier<IStorageRepository.Keyed<UUID, Q, ?>> storageRepositorySupplier;
    private final Supplier<D> createNew;
    private final ThrownBiConsumer<UUID, D, Exception> save;
    private final ThrownFunction<Q, Map<UUID, D>, Exception> getAll;
    private final ThrownFunction<Q, Optional<KeyedObject<UUID, D>>, Exception> getQuery;
    private final ThrownFunction<UUID, Optional<D>, Exception> get;
    private final PluginContainer pluginContainer;

    public <O> AbstractKeyedService(
        final Supplier<IDataTranslator<D, O>> dts,
        final Supplier<IStorageRepository.Keyed<UUID, Q, O>> srs,
        final PluginContainer pluginContainer
    ) {
        this(
                () -> dts.get().createNew(),
                (key, udo) -> srs.get().save(
                        key,
                        dts.get().toDataAccessObject(udo)
                ),
                query -> srs.get()
                        .getAll(query)
                        .entrySet().stream()
                        .filter(x -> x.getValue() != null)
                        .collect(
                                ImmutableMap.toImmutableMap(
                                        Map.Entry::getKey,
                                        x -> dts.get().fromDataAccessObject(x.getValue())
                                )
                        ),
                uuid -> srs.get().get(uuid).map(dts.get()::fromDataAccessObject),
                query -> srs.get().get(query).map(x -> x.mapValue(dts.get()::fromDataAccessObject)),
                srs::get,
                pluginContainer);
    }

    private AbstractKeyedService(
            final Supplier<D> createNew,
            final ThrownBiConsumer<UUID, D, Exception> save,
            final ThrownFunction<Q, Map<UUID, D>, Exception> getAll,
            final ThrownFunction<UUID, Optional<D>, Exception> get,
            final ThrownFunction<Q, Optional<KeyedObject<UUID, D>>, Exception> getQuery,
            final Supplier<IStorageRepository.Keyed<UUID, Q, ?>> storageRepositorySupplier,
            final PluginContainer pluginContainer
    ) {
        this.pluginContainer = pluginContainer;
        this.createNew = createNew;
        this.save = save;
        this.getAll = getAll;
        this.get = get;
        this.getQuery = getQuery;
        this.storageRepositorySupplier = storageRepositorySupplier;
    }

    public D createNew() {
        return this.createNew.get();
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        this.cache.invalidateAll();
        return ServicesUtil.run(() -> {
            this.storageRepositorySupplier.get().clearCache();
            return null;
        }, this.pluginContainer);
    }

    @Override
    public CompletableFuture<Optional<D>> get(@Nonnull final UUID key) {
        final ReentrantReadWriteLock.ReadLock lock = this.dataLocks.get(key).readLock();
        try {
            lock.lock();
            final D result = this.cache.getIfPresent(key);
            this.dirty.add(key);
            if (result != null) {
                return CompletableFuture.completedFuture(Optional.of(result));
            }
        } finally {
            lock.unlock();
        }

        return ServicesUtil.run(() -> this.getFromRepo(key), this.pluginContainer);
    }

    @Override
    public Optional<D> getOnThread(@Nonnull final UUID key) {
        // Read lock for the cache
        final ReentrantReadWriteLock.ReadLock lock = this.dataLocks.get(key).readLock();
        try {
            lock.lock();
            final D result = this.cache.getIfPresent(key);
            this.dirty.add(key);
            if (result != null) {
                return Optional.of(result);
            }
        } finally {
            lock.unlock();
        }

        try {
            return this.getFromRepo(key);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<D> getFromRepo(@Nonnull final UUID key) throws Exception {
        // Write lock because of the cache
        final ReentrantReadWriteLock.WriteLock lock = this.dataLocks.get(key).writeLock();
        try {
            lock.lock();
            final Optional<D> r = this.get.apply(key);
            r.ifPresent(d -> this.cache.put(key, d));
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<KeyedObject<UUID, D>>> get(@Nonnull final Q query) {
        return ServicesUtil.run(() -> {
            final Optional<KeyedObject<UUID, D>> r = this.getQuery.apply(query);
            r.ifPresent(d -> {
                if (d.getValue().isPresent()) {
                    this.cache.put(d.getKey(), d.getValue().get());
                    this.dirty.add(d.getKey());
                } else {
                    this.cache.invalidate(d.getKey());
                }
            });
            return r;
        }, this.pluginContainer);
    }

    @Override
    public CompletableFuture<Map<UUID, D>> getAll(@Nonnull final Q query) {
        return ServicesUtil.run(() -> {
            final Map<UUID, D> res = this.getAll.apply(query);
            /* Map<UUID, D> res = r.entrySet().stream()
                    .filter(x -> x.getValue() != null)
                    .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, v -> dataAccess.fromDataAccessObject(v.getValue()))); */
            res.forEach((k, v) -> {
                this.cache.put(k, v);
                this.dirty.add(k);
            });
            return res;
        }, this.pluginContainer);
    }

    @Override
    public CompletableFuture<Boolean> exists(@Nonnull final UUID key) {
        return ServicesUtil.run(() -> this.storageRepositorySupplier.get().exists(key), this.pluginContainer);
    }

    @Override
    public CompletableFuture<Integer> count(@Nonnull final Q query) {
        return ServicesUtil.run(() -> this.storageRepositorySupplier.get().count(query), this.pluginContainer);
    }

    @Override
    public <T2> CompletableFuture<Void> setAndSave(@Nonnull final UUID key, final DataKey<T2, ? extends D> dataKey, final T2 data) {
        return this.getOrNew(key).thenAccept(x -> {
            x.set(dataKey, data);
            this.save(key, x);
        });
    }

    @Override
    public CompletableFuture<Void> save(@Nonnull final UUID key, @Nonnull final D value) {
        return ServicesUtil.run(() -> {
            final ReentrantReadWriteLock reentrantReadWriteLock = this.dataLocks.get(key);
            final ReentrantReadWriteLock.WriteLock lock = reentrantReadWriteLock.writeLock();
            try {
                lock.lock();
                this.cache.put(key, value);
            } finally {
                lock.unlock();
            }
            this.save.apply(key, value);
            this.dirty.remove(key);
            return null;
        }, this.pluginContainer);
    }

    @Override
    public CompletableFuture<Void> delete(@Nonnull final UUID key) {
        return ServicesUtil.run(() -> {
            final ReentrantReadWriteLock reentrantReadWriteLock = this.dataLocks.get(key);
            final ReentrantReadWriteLock.WriteLock lock = reentrantReadWriteLock.writeLock();
            try {
                lock.lock();
                this.storageRepositorySupplier.get().delete(key);
                this.cache.invalidate(key);
                return null;
            } finally {
                lock.unlock();
            }
        }, this.pluginContainer);
    }

    @Override
    public CompletableFuture<Void> ensureSaved() {
        return ServicesUtil.run(() -> {
            for (final UUID uuid : ImmutableSet.copyOf(this.dirty)) {
                final D d = this.cache.getIfPresent(uuid);
                if (d != null) {
                    this.save(uuid, d);
                } else {
                    this.dirty.remove(uuid);
                }
            }
            return null;
        }, this.pluginContainer);
    }
}
