package io.github.nucleuspowered.nucleus;

import com.google.inject.Injector;
import io.github.nucleuspowered.nucleus.guice.NucleusInjectorModule;
import io.github.nucleuspowered.nucleus.module.IModuleProvider;
import io.github.nucleuspowered.nucleus.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.NucleusServiceCollection;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterFactoryEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Collection;

public final class NucleusCore {

    private final PluginContainer pluginContainer;
    private final Path configDirectory;
    private final Logger logger;
    private final Injector injector;
    private final IModuleProvider provider;

    private final INucleusServiceCollection serviceCollection;

    public NucleusCore(final PluginContainer pluginContainer,
            final Path configDirectory,
            final Logger logger,
            final Injector injector,
            final IModuleProvider provider) {
        this.pluginContainer = pluginContainer;
        this.configDirectory = configDirectory;
        this.logger = logger;
        this.injector = injector.createChildInjector(new NucleusInjectorModule(() -> this));
        this.provider = provider;
        this.serviceCollection = new NucleusServiceCollection(
                this.injector,
                this.pluginContainer,
                this.logger,
                this::getDataDirectory,
                this.configDirectory
        );
    }

    /**
     * Begin setup, read module config, load modules that are important.
     */
    public void init() {

    }

    public INucleusServiceCollection getServiceCollection() {
        return this.serviceCollection;
    }

    public Path getDataDirectory() {
        return Sponge.getServer().getGame().getGameDirectory();
    }

    public Path getConfigDirectory() {
        return this.configDirectory;
    }

    @Listener
    public void establishFactories(final RegisterFactoryEvent event) {

    }

    @Listener
    public void establishCommands(final RegisterCommandEvent<Command.Parameterized> event) {

    }

    @Listener
    public void serverStarting(final StartingEngineEvent<Server> event) {

    }

    @Listener
    public void serverStarted(final StartedEngineEvent<Server> event) {

    }

    @Listener
    public void serverStopping(final StoppingEngineEvent<Server> event) {

    }

    // -- Module loading

    private void startModuleLoading() {
        final Collection<ModuleContainer> moduleContainerCollection = this.provider.getModules();
    }

}
