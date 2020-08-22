/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IModuleDataProvider;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The base module that Nucleus will use to construct its basic services.
 */
public class NucleusInjectorModule extends AbstractModule {

    private final Supplier<INucleusServiceCollection> serviceCollection;
    private final Supplier<Path> dataDirectory;
    private final Path configDirectory;
    private final IModuleDataProvider moduleDataProvider;
    private final Supplier<DiscoveryModuleHolder<?, ?>> discoveryModuleHolderSupplier;

    public NucleusInjectorModule(
            final Supplier<INucleusServiceCollection> serviceCollection,
            final Supplier<Path> dataDirectory,
            final Supplier<DiscoveryModuleHolder<?, ?>> discoveryModuleHolderSupplier,
            final Path configDirectory,
            final IModuleDataProvider moduleDataProvider) {
        this.dataDirectory = dataDirectory;
        this.configDirectory = configDirectory;
        this.moduleDataProvider = moduleDataProvider;
        this.serviceCollection = serviceCollection;
        this.discoveryModuleHolderSupplier = discoveryModuleHolderSupplier;
    }

    @Override protected void configure() {
        this.bind(new TypeLiteral<Supplier<Path>>() {}).annotatedWith(DataDirectory.class).toInstance(this.dataDirectory);
        this.bind(Path.class).annotatedWith(ConfigDirectory.class).toInstance(this.configDirectory);
        this.bind(IModuleDataProvider.class).toInstance(this.moduleDataProvider);
    }

    @Provides
    private INucleusServiceCollection provideServiceCollection() {
        return this.serviceCollection.get();
    }

    @Provides
    private Supplier<DiscoveryModuleHolder<? ,?>> getModuleHolder() {
        return this.discoveryModuleHolderSupplier;
    }

}
