package io.github.nucleuspowered.nucleus.modules.kit.storage;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataaccess.IConfigurateBackedDataTranslator;
import io.github.nucleuspowered.nucleus.services.impl.storage.services.SingleCachedService;
import io.github.nucleuspowered.storage.IStorageModule;
import io.github.nucleuspowered.storage.persistence.IStorageRepository;
import io.github.nucleuspowered.storage.persistence.IStorageRepositoryFactory;
import io.github.nucleuspowered.storage.services.IStorageService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class KitStorageModule implements IStorageModule<IKitDataObject, IStorageService.Single<IKitDataObject>,
        IStorageRepository.Single<JsonObject>, IConfigurateBackedDataTranslator<IKitDataObject>> {

    private final INucleusServiceCollection serviceCollection;
    private final IStorageService.SingleCached<IKitDataObject> kitsService;
    private IStorageRepository.@Nullable Single<JsonObject> repository;

    private final IConfigurateBackedDataTranslator<IKitDataObject> kitsDataAccess = new IConfigurateBackedDataTranslator<IKitDataObject>() {
        @Override public ConfigurationNode createNewNode() {
            return SimpleConfigurationNode.root(KitStorageModule.this.serviceCollection.configurateHelper().setOptions(ConfigurationOptions.defaults()));
        }

        @Override public IKitDataObject createNew() {
            final KitDataObject d = new KitDataObject();
            d.setBackingNode(KitStorageModule.this.serviceCollection.configurateHelper().createNode());
            return d;
        }
    };

    @Inject
    public KitStorageModule(final INucleusServiceCollection serviceCollection) {
        this.kitsService = new SingleCachedService<>(
                this::getRepository,
                this::getDataTranslator,
                serviceCollection.pluginContainer());
        this.serviceCollection = serviceCollection;
    }

    @Override
    public IStorageService.Single<IKitDataObject> getService() {
        return this.kitsService;
    }

    @Override
    public IConfigurateBackedDataTranslator<IKitDataObject> getDataTranslator() {
        return this.kitsDataAccess;
    }

    @Override
    public IStorageRepository.Single<JsonObject> getRepository() {
        if (this.repository == null) {
            this.repository = this.serviceCollection.storageManager().getFlatFileRepositoryFactory().kitsRepository();
        }
        return this.repository;
    }

    @Override
    public void setRepository(final IStorageRepositoryFactory factory) {
        this.repository = factory.kitsRepository();
    }

    @Override
    public void detach() {
        if (this.repository != null) {
            this.repository.shutdown();
        }
        this.repository = null;
    }

    public IKitDataObject getKits() {
        final IStorageService.SingleCached<IKitDataObject> gs = this.kitsService;
        return gs.getCached().orElseGet(() -> gs.getOrNew().join());
    }

}
