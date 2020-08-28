package io.github.nucleuspowered.nucleus;

import com.google.inject.Injector;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import io.github.nucleuspowered.nucleus.core.teleport.scanners.NoTeleportScanner;
import io.github.nucleuspowered.nucleus.core.teleport.scanners.VerticalTeleportScanner;
import io.github.nucleuspowered.nucleus.guice.NucleusInjectorModule;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.module.IModuleProvider;
import io.github.nucleuspowered.nucleus.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifierFactory;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.CooldownModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.CostModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.RequiresEconomyModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.WarmupModifier;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerReloadableWrapper;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.NucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;
import org.spongepowered.api.event.lifecycle.RegisterCatalogRegistryEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterFactoryEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        this.registerCore();
        this.startModuleLoading();
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
    public void establishNewRegistries(final RegisterCatalogRegistryEvent event) {
        event.register(CommandModifierFactory.class, ResourceKey.of("nucleus", "command_modifier_factory"));
        event.register(TeleportScanner.class, ResourceKey.of("nucleus", "teleport_scanner"));
    }

    @Listener
    public void registerCommandModifierFactories(final RegisterCatalogEvent<CommandModifierFactory> event) {
        event.register(new CommandModifierFactory.Simple(new CooldownModifier()));
        event.register(new CommandModifierFactory.Simple(new CostModifier()));
        event.register(new CommandModifierFactory.Simple(new WarmupModifier()));
        event.register(new CommandModifierFactory.Simple(new RequiresEconomyModifier()));
    }

    @Listener
    public void registerTeleportScanners(final RegisterCatalogEvent<TeleportScanner> event) {
        event.register(new NoTeleportScanner());
        event.register(new VerticalTeleportScanner.Ascending());
        event.register(new VerticalTeleportScanner.Descending());
    }

    @Listener
    public void establishFactories(final RegisterFactoryEvent event) {

    }

    @Listener(order = Order.LAST)
    public void establishCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        this.serviceCollection.commandMetadataService().completeRegistrationPhase(this.serviceCollection, event);
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

    private void registerCore() {

    }

    private void startModuleLoading() {
        final Collection<ModuleContainer> moduleContainerCollection = this.provider.getModules();
        for (final ModuleContainer container : this.filterModules(moduleContainerCollection)) {
            final IModule module;
            try {
                module = container.construct(this.injector);
            } catch (final RuntimeException e) {
                this.logger.error("Could not load module {}. Skipping...", container.getId(), e);
                continue;
            }

            module.init();
            if (module instanceof IModule.Configurable) {
                this.serviceCollection.moduleDataProvider().registerModuleConfig(container.getId(), ((IModule.Configurable<?>) module).getConfigClass());
            }

            // listeners
            Sponge.getEventManager().registerListeners(this.pluginContainer, module);
            for (final ListenerBase listener : module.getListeners()) {
                if (listener instanceof ListenerBase.Conditional) {
                    this.serviceCollection.reloadableService().registerReloadable(new ListenerReloadableWrapper((ListenerBase.Conditional) listener));
                } else {
                    Sponge.getEventManager().registerListeners(this.pluginContainer, listener);
                }
            }

            // commands
            this.serviceCollection.commandMetadataService().registerCommands(container.getId(), container.getName(), module.getCommands());
            this.serviceCollection.commandMetadataService().registerInterceptors(module.getCommandInterceptors());

            // tasks
            for (final TaskBase taskBase : module.getTasks()) {
                // TODO: Async tasks
                Task.builder().plugin(this.pluginContainer)
                        .delay(taskBase.interval())
                        .interval(taskBase.interval())
                        .name("Nucleus - " + taskBase.getClass().getSimpleName())
                        .build();
            }

            // Player info service
            module.getInfoProvider().ifPresent(x -> this.serviceCollection.playerInformationService().registerProvider(x));

            // Register permissions in the description builder
            module.getPermissions().ifPresent(x -> this.serviceCollection.permissionService().register(container.getId(), x));

        }

    }

    private <T> void registerConfig(final String id, final IModule.Configurable<T> module) {
        this.serviceCollection.moduleDataProvider().registerModuleConfig(id, module.getConfigClass());
    }

    private Collection<ModuleContainer> filterModules(final Collection<ModuleContainer> moduleContainers) {
        final CommentedConfigurationNode defaults = this.serviceCollection.configurateHelper().createNode();
        for (final ModuleContainer moduleContainer : moduleContainers) {
            defaults.getNode(moduleContainer.getId()).setValue(true);
        }

        final ConfigurationLoader<CommentedConfigurationNode> moduleConfig = HoconConfigurationLoader.builder()
                .setPath(this.configDirectory.resolve("modules.conf"))
                .build();

        CommentedConfigurationNode node;
        try {
            node = moduleConfig.load(this.serviceCollection.configurateHelper().getOptions());
            node.mergeValuesFrom(defaults);
            moduleConfig.save(node);
        } catch (final IOException e) {
            node = defaults;
            this.logger.error("Could not load module config. Defaulting all to TRUE.", e);
        }

        // TODO: Fire event

        final CommentedConfigurationNode finalNode = node;
        return moduleContainers.stream().filter(x -> finalNode.getNode(x.getId()).getBoolean(false)).collect(Collectors.toList());
    }

}
