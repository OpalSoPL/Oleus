package io.github.nucleuspowered.storage;

import com.google.gson.JsonObject;
import io.github.nucleuspowered.storage.dataaccess.IDataTranslator;
import io.github.nucleuspowered.storage.dataobjects.IDataObject;
import io.github.nucleuspowered.storage.persistence.IStorageRepository;
import io.github.nucleuspowered.storage.persistence.IStorageRepositoryFactory;
import io.github.nucleuspowered.storage.services.IStorageService;

public interface IStorageModule<T extends IDataObject, S extends IStorageService<T>, R extends IStorageRepository,
        D extends IDataTranslator<T, JsonObject>> {

    S getService();

    D getDataTranslator();

    R getRepository();

    void setRepository(IStorageRepositoryFactory factory);

    void detach();
}
