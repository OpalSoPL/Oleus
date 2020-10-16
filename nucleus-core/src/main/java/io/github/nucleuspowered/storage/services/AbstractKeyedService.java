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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractKeyedService<K, Q extends IQueryObject<K, Q>, D extends IKeyedDataObject<D>>
        implements IStorageService.Keyed.KeyedData<K, Q, D> {

    private final LoadingCache<K, ReentrantReadWriteLock> dataLocks =
            Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<K, ReentrantReadWriteLock>() {
                @NonNull
                @Override
                public ReentrantReadWriteLock load(@NonNull final K key) {
                    return new ReentrantReadWriteLock();
                }
            });
    private final Cache<K, D> cache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();
    private final Set<K> dirty = new HashSet<>();

    private final Supplier<IStorageRepository.Keyed<K, Q, ?>> storageRepositorySupplier;
    private final Supplier<D> createNew;
    private final ThrownBiConsumer<K, D, Exception> save;
    private final ThrownFunction<Q, Map<K, D>, Exception> getAll;
    private final ThrownFunction<Q, Optional<KeyedObject<K, D>>, Exception> getQuery;
    private final ThrownFunction<K, Optional<D>, Exception> get;
    private final PluginContainer pluginContainer;
    private final Consumer<D> upgrader;
    private final Consumer<D> versionSetter;

    public <O> AbstractKeyedService(
        final Supplier<IDataTranslator<D, O>> dts,
        final Supplier<IStorageRepository.Keyed<K, Q, O>> srs,
        final Consumer<D> upgrader,
        final Consumer<D> versionSetter,
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
                K -> srs.get().get(K).map(dts.get()::fromDataAccessObject),
                query -> srs.get().get(query).map(x -> x.mapValue(dts.get()::fromDataAccessObject)),
                srs::get,
                upgrader,
                versionSetter,
                pluginContainer);
    }

    private AbstractKeyedService(
            final Supplier<D> createNew,
            final ThrownBiConsumer<K, D, Exception> save,
            final ThrownFunction<Q, Map<K, D>, Exception> getAll,
            final ThrownFunction<K, Optional<D>, Exception> get,
            final ThrownFunction<Q, Optional<KeyedObject<K, D>>, Exception> getQuery,
            final Supplier<IStorageRepository.Keyed<K, Q, ?>> storageRepositorySupplier,
            final Consumer<D> upgrader,
            final Consumer<D> versionSetter,
            final PluginContainer pluginContainer
    ) {
        this.pluginContainer = pluginContainer;
        this.createNew = createNew;
        this.save = save;
        this.getAll = getAll;
        this.get = get;
        this.getQuery = getQuery;
        this.upgrader = upgrader;
        this.versionSetter = versionSetter;
        this.storageRepositorySupplier = storageRepositorySupplier;
    }

    public D createNew() {
        final D data = this.createNew.get();
        this.versionSetter.accept(data);
        return data;
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
    public CompletableFuture<Optional<D>> get(@NonNull final K key) {
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
    public Optional<D> getOnThread(@NonNull final K key) {
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

    private Optional<D> getFromRepo(@NonNull final K key) throws Exception {
        // Write lock because of the cache
        final ReentrantReadWriteLock.WriteLock lock = this.dataLocks.get(key).writeLock();
        try {
            lock.lock();
            final Optional<D> r = this.get.apply(key);
            r.ifPresent(d -> {
                this.upgrader.accept(d);
                this.cache.put(key, d);
            });
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<KeyedObject<K, D>>> get(@NonNull final Q query) {
        return ServicesUtil.run(() -> {
            final Optional<KeyedObject<K, D>> r = this.getQuery.apply(query);
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
    public CompletableFuture<Map<K, D>> getAll(@NonNull final Q query) {
        return ServicesUtil.run(() -> {
            final Map<K, D> res = this.getAll.apply(query);
            /* Map<K, D> res = r.entrySet().stream()
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
    public CompletableFuture<Boolean> exists(@NonNull final K key) {
        return ServicesUtil.run(() -> this.storageRepositorySupplier.get().exists(key), this.pluginContainer);
    }

    @Override
    public CompletableFuture<Integer> count(@NonNull final Q query) {
        return ServicesUtil.run(() -> this.storageRepositorySupplier.get().count(query), this.pluginContainer);
    }

    @Override
    public <T2> CompletableFuture<Void> setAndSave(@NonNull final K key, final DataKey<T2, ? extends D> dataKey, final T2 data) {
        return this.getOrNew(key).thenAccept(x -> {
            x.set(dataKey, data);
            this.save(key, x);
        });
    }

    @Override
    public CompletableFuture<Void> save(@NonNull final K key, @NonNull final D value) {
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
    public CompletableFuture<Void> delete(@NonNull final K key) {
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
            for (final K K : ImmutableSet.copyOf(this.dirty)) {
                final D d = this.cache.getIfPresent(K);
                if (d != null) {
                    this.save(K, d);
                } else {
                    this.dirty.remove(K);
                }
            }
            return null;
        }, this.pluginContainer);
    }
}
