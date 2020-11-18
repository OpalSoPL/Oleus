/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.gson.JsonObject;
import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.storage.StorageManager;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IGeneralDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IWorldDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.queryobjects.IUserQueryObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.queryobjects.IWorldQueryObject;
import io.github.nucleuspowered.storage.IStorageModule;
import io.github.nucleuspowered.storage.dataaccess.IDataTranslator;
import io.github.nucleuspowered.storage.dataobjects.IDataObject;
import io.github.nucleuspowered.storage.persistence.IStorageRepository;
import io.github.nucleuspowered.storage.persistence.IStorageRepositoryFactory;
import io.github.nucleuspowered.storage.services.IStorageService;
import org.spongepowered.api.ResourceKey;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ImplementedBy(StorageManager.class)
public interface IStorageManager {

    IStorageRepositoryFactory getFlatFileRepositoryFactory();

    // ugh
    <T extends IDataObject, S extends IStorageService<T>> void register(
            final IStorageModule<T, S, ? extends IStorageRepository, ? extends IDataTranslator<T, JsonObject>> module);

    IStorageService.SingleCached<IGeneralDataObject> getGeneralService();

    <T extends IDataObject, S extends IStorageService<T>, M extends IStorageModule<T, S, ?, ?>> Optional<S> getAdditionalStorageServiceForDataObject(
            final Class<M> clazz);

    IStorageService.Keyed.KeyedData<UUID, IUserQueryObject, IUserDataObject> getUserService();

    IStorageService.Keyed.KeyedData<ResourceKey, IWorldQueryObject, IWorldDataObject> getWorldService();

    IDataTranslator<IUserDataObject, JsonObject> getUserDataAccess();

    IDataTranslator<IWorldDataObject, JsonObject> getWorldDataAccess();

    IDataTranslator<IGeneralDataObject, JsonObject> getGeneralDataAccess();

    IStorageRepository.Keyed<UUID, IUserQueryObject, JsonObject> getUserRepository();

    IStorageRepository.Keyed<ResourceKey, IWorldQueryObject, JsonObject> getWorldRepository();

    IStorageRepository.Single<JsonObject> getGeneralRepository();

    CompletableFuture<Void> saveAndInvalidateAllCaches();

    default CompletableFuture<IUserDataObject> getOrCreateUser(final UUID uuid) {
        return this.getUserService().getOrNew(uuid);
    }

    default IUserDataObject getOrCreateUserOnThread(final UUID uuid) {
        return this.getUserService().getOrNewOnThread(uuid);
    }

    default CompletableFuture<Optional<IUserDataObject>> getUser(final UUID uuid) {
        return this.getUserService().get(uuid);
    }

    default Optional<IUserDataObject> getUserOnThread(final UUID uuid) {
        return this.getUserService().getOnThread(uuid);
    }

    default CompletableFuture<Void> saveUser(final UUID uuid, final IUserDataObject object) {
        return this.getUserService().save(uuid, object);
    }

    default Optional<IWorldDataObject> getWorldOnThread(final ResourceKey key) {
        return this.getWorldService().getOnThread(key);
    }

    default IWorldDataObject getOrCreateWorldOnThread(final ResourceKey key) {
        return this.getWorldService().getOrNewOnThread(key);
    }

    default IGeneralDataObject getGeneral() {
        final IStorageService.SingleCached<IGeneralDataObject> gs = this.getGeneralService();
        return gs.getCached().orElseGet(() -> gs.getOrNew().join());
    }

    CompletableFuture<Void> saveAll();

    void attachAll();

    void detachAll();
}
