/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.typesafe.config.ConfigException;
import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.api.core.NucleusWarmupManagerService;
import io.github.nucleuspowered.nucleus.api.placeholder.NucleusPlaceholderService;
import io.github.nucleuspowered.nucleus.api.teleport.NucleusSafeTeleportService;
import io.github.nucleuspowered.nucleus.guice.NucleusInjectorModule;
import io.github.nucleuspowered.nucleus.quickstart.ModuleRegistrationProxyService;
import io.github.nucleuspowered.nucleus.quickstart.NucleusLoggerProxy;
import io.github.nucleuspowered.nucleus.quickstart.QuickStartModuleConstructor;
import io.github.nucleuspowered.nucleus.quickstart.event.BaseModuleEvent;
import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.registry.TeleportScannerRegistryModule;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModiferRegistry;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.NucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.commandmetadata.CommandMetadataService;
import io.github.nucleuspowered.nucleus.services.impl.moduledata.ModuleDataProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigurateHelper;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IModuleDataProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.services.interfaces.IStorageManager;
import io.github.nucleuspowered.nucleus.util.ClientMessageReciever;
import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.PluginContainer;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;
import uk.co.drnaylor.quickstart.holders.discoverystrategies.Strategy;
import uk.co.drnaylor.quickstart.loaders.ModuleEnablerBuilder;
import uk.co.drnaylor.quickstart.loaders.PhasedModuleEnabler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class NucleusCore {

    private static final String DOCGEN_PROPERTY = "nucleus.docgen";
    private static final String NO_VERSION_CHECK = "nucleus.nocheck";

    private static final String divider = "+------------------------------------------------------------+";
    private static final int length = divider.length() - 2;

    private final INucleusServiceCollection serviceCollection;
    private final Logger logger;
    private final PluginContainer pluginContainer;

    private boolean hasStarted = false;
    private Throwable isErrored = null;
    private final List<Component> startupMessages = Lists.newArrayList();

    private DiscoveryModuleHolder<StandardModule, StandardModule> moduleContainer;

    private final Path configDir;
    private final Supplier<Path> dataDir;
    @Nullable private Path dataFileLocation = null;
    private boolean isServer = false;
    @Nullable private String versionFail;
    private final boolean docgenOnly;
    private final boolean shutdownAtLoadEnd;

    private static final ImmutableSet<String> requiredClasses = ImmutableSet.of("org.spongepowered.api.text.placeholder.PlaceholderParser");

    private static boolean versionCheck(final IMessageProviderService provider) throws IllegalStateException {
        if (!requiredClasses.isEmpty()) {
            for (final String str : requiredClasses) {
                try {
                    Class.forName(str);
                } catch (final ClassNotFoundException e) {
                    throw new IllegalStateException(provider.getMessageString("startup.nostart.missingclass", str));
                }
            }
            return true;
        }

        if (System.getProperty(NO_VERSION_CHECK) != null) {
            final Pattern matching = Pattern.compile("^(?<major>\\d+)\\.(?<minor>\\d+)");
            final Optional<String> v = Sponge.getPlatform().getContainer(Platform.Component.API).getVersion();

            if (v.isPresent()) {
                final Matcher version = matching.matcher(NucleusPluginInfo.SPONGE_API_VERSION);
                if (!version.find()) {
                    return false; // can't compare.
                }

                final int maj = Integer.parseInt(version.group("major"));
                final int min = Integer.parseInt(version.group("minor"));
                //noinspection MismatchedStringCase,ConstantConditions
                final boolean notRequiringSnapshot = !NucleusPluginInfo.SPONGE_API_VERSION.contains("SNAPSHOT");

                final Matcher m = matching.matcher(v.get());
                if (m.find()) {
                    final int major = Integer.parseInt(m.group("major"));
                    if (major != maj) {
                        // not current API
                        throw new IllegalStateException(provider.getMessageString("startup.nostart.spongeversion.major",
                                NucleusPluginInfo.NAME, v.get(), NucleusPluginInfo.SPONGE_API_VERSION,
                                Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName()));
                    }

                    int minor = Integer.parseInt(m.group("minor"));
                    final boolean serverIsSnapshot = v.get().contains("SNAPSHOT");

                    //noinspection ConstantConditions
                    if (serverIsSnapshot && notRequiringSnapshot) {
                        // If we are a snapshot, and the target version is NOT a snapshot, decrement our version number.
                        minor = minor - 1;
                    }

                    if (minor < min) {
                        // not right minor version
                        throw new IllegalStateException(provider.getMessageString("startup.nostart.spongeversion.minor",
                                Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName(), NucleusPluginInfo.NAME,
                                NucleusPluginInfo.SPONGE_API_VERSION));
                    }
                }

                return true;
            } else {
                // no idea.
                return false;
            }
        }
        return true;
    }

    // We inject this into the constructor so we can build the config path ourselves.
    @Inject
    public NucleusCore(
            final PluginContainer pluginContainer,
            @ConfigDir(sharedRoot = true) final Path configDir,
            final Logger logger,
            final Injector injector) {
        this.logger = logger;
        this.configDir = configDir.resolve(NucleusPluginInfo.ID);
        this.dataDir = () -> Sponge.getGame().getSavesDirectory().resolve("nucleus");
        this.pluginContainer = pluginContainer;
        final IModuleDataProvider moduleDataProvider = new ModuleDataProvider(() -> this.moduleContainer);
        final Injector baseInjector = injector.createChildInjector(
                new NucleusInjectorModule(
                        this::getServiceCollection,
                        this.dataDir,
                        this::getDiscoveryModuleHolder,
                        this.configDir,
                        moduleDataProvider));
        this.serviceCollection = new NucleusServiceCollection(
                baseInjector,
                pluginContainer,
                logger,
                this.dataDir,
                this.configDir);
        this.docgenOnly = System.getProperty(DOCGEN_PROPERTY) != null;
        this.shutdownAtLoadEnd = System.getProperty(DOCGEN_PROPERTY) != null;
    }

    private INucleusServiceCollection getServiceCollection() {
        return this.serviceCollection;
    }

    private DiscoveryModuleHolder<?, ?> getDiscoveryModuleHolder() {
        return this.moduleContainer;
    }

    @Listener(order = Order.FIRST)
    public void onPreInit(final GamePreInitializationEvent preInitializationEvent) {
        // Create the command modifier registry module and start it.
        final CommandModiferRegistry registry = new CommandModiferRegistry();
        registry.registerDefaults();
        registry.getAll().forEach(x -> {
            if (x instanceof IReloadableService.Reloadable) {
                this.serviceCollection.reloadableService().registerReloadable((IReloadableService.Reloadable) x);
            }
        });

        // Other registry stuff
        final TeleportScannerRegistryModule registryModule = new TeleportScannerRegistryModule();
        registryModule.registerDefaults();

        // Compatibility
        final Optional<Asset> compatJson = Sponge.getAssetManager().getAsset(this.pluginContainer, "compat.json");
        compatJson.ifPresent(x -> {
            try {
                final JsonArray object = new JsonParser().parse(x.readString())
                        .getAsJsonObject()
                        .getAsJsonObject("json")
                        .getAsJsonArray("messages");
                this.serviceCollection.compatibilityService().set(object);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

        final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
        // Setup object mapper.
        final MessageReceiver s;
        if (Sponge.getGame().isServerAvailable()) {
            s = Sponge.getServer().getConsole();
        } else {
            s = new ClientMessageReciever(this.logger);
        }

        // From the config, get the `core.language` entry, if it exists.
        final HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder().setPath(Paths.get(this.configDir.toString(), "main.conf"));
        try {
            final CommentedConfigurationNode node = builder.build().load();
            final String location = node.getNode("core", "data-file-location").getString("default");
            if (!location.equalsIgnoreCase("default")) {
                this.dataFileLocation = Paths.get(location);
            }
        } catch (final IOException e) {
            // don't worry about it
        }

        try {
            if (!versionCheck(messageProvider)) {
                s.sendMessage(
                        messageProvider.getMessage("startup.nostart.nodetect", NucleusPluginInfo.NAME, NucleusPluginInfo.SPONGE_API_VERSION));
            }
        } catch (final IllegalStateException e) {
            s.sendMessage(messageProvider.getMessage("startup.nostart.compat", NucleusPluginInfo.NAME,
                    Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName(),
                    Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse("unknown")));
            s.sendMessage(messageProvider.getMessage("startup.nostart.compat2", e.getMessage()));
            s.sendMessage(messageProvider.getMessage("startup.nostart.compat3", NucleusPluginInfo.NAME));
            this.versionFail = e.getMessage();
            this.disable();
            return;
        }

        // Deferred for version check.
        Sponge.getEventManager().registerListeners(this.serviceCollection.pluginContainer(), new NucleusRegistration(this.serviceCollection));
        s.sendMessage(messageProvider.getMessage("startup.welcome", NucleusPluginInfo.NAME,
                NucleusPluginInfo.VERSION, Sponge.getPlatform().getContainer(Platform.Component.API).getVersion().orElse("unknown")));

        this.logger.info(messageProvider.getMessageString("startup.preinit", NucleusPluginInfo.NAME));
        final Game game = Sponge.getGame();

        // Get the mandatory config files.
        try {
            NucleusAPITokens.onPreInit(this);
            Files.createDirectories(this.configDir);
            if (this.isServer) {
                Files.createDirectories(this.dataDir.get());
            }
        } catch (final Exception e) {
            this.isErrored = e;
            this.disable();
            e.printStackTrace();
            return;
        }

        game.getServiceManager().setProvider(this.pluginContainer, ModuleRegistrationProxyService.class, new ModuleRegistrationProxyService(this.serviceCollection,
                this.moduleContainer));
        game.getServiceManager().setProvider(this.pluginContainer, NucleusWarmupManagerService.class, this.serviceCollection.warmupService());
        game.getServiceManager().setProvider(this.pluginContainer, NucleusUserPreferenceService.class, this.serviceCollection.userPreferenceService());
        game.getServiceManager().setProvider(this.pluginContainer, NucleusSafeTeleportService.class, this.serviceCollection.teleportService());
        game.getServiceManager().setProvider(this.pluginContainer, NucleusPlaceholderService.class, this.serviceCollection.placeholderService());

        try {
            final String he = this.serviceCollection.messageProvider().getMessageString("config.main-header", NucleusPluginInfo.VERSION);
            final Optional<Asset> optionalAsset = Sponge.getAssetManager().getAsset(this.pluginContainer, "classes.json");
            final DiscoveryModuleHolder.Builder<StandardModule, StandardModule> db =
                    DiscoveryModuleHolder.builder(StandardModule.class, StandardModule.class);
            if (optionalAsset.isPresent()) {
                final Map<String, Map<String, List<String>>> m = new Gson().fromJson(
                        optionalAsset.get().readString(),
                        new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType()
                );

                final Set<Class<?>> sc = Sets.newHashSet();
                for (final String classString : m.keySet()) {
                    sc.add(Class.forName(classString));
                }

                db.setStrategy((string, classloader) -> sc)
                        .setConstructor(new QuickStartModuleConstructor(m, this.serviceCollection));
            } else {
                db.setConstructor(new QuickStartModuleConstructor(null, this.serviceCollection))
                        .setStrategy(Strategy.DEFAULT);
            }

            final PhasedModuleEnabler<StandardModule, StandardModule> enabler =
                    new ModuleEnablerBuilder<>(StandardModule.class, StandardModule.class)
                            .createPreEnablePhase("preenable", holder -> Sponge.getEventManager().post(new BaseModuleEvent.AboutToEnable(this)))
                            .createEnablePhase("config", (module, holder) -> module.configTasks())
                            .createEnablePhase("permissions", (module, holder) -> module.registerPermissions())
                            .createEnablePhase("reg", (module, holder) -> module.loadRegistries())
                            .createEnablePhase("services", (module, holder) -> module.loadServices())
                            .createEnablePhase("pre-tasks", (module, holder) -> module.performPreTasks(this.serviceCollection))
                            .createPreEnablePhase("enable", holder -> Sponge.getEventManager().post(new BaseModuleEvent.PreEnable(this)))
                            .createEnablePhase("command-discovery", (module, holder) -> module.loadCommands())
                            .createEnablePhase("aliased-commands", (module, holder) -> module.prepareAliasedCommands())
                            .createEnablePhase("events", (module, holder) -> module.loadEvents())
                            .createEnablePhase("runnables", (module, holder) -> module.loadRunnables())
                            .createEnablePhase("infoproviders", (module, holder) -> module.loadInfoProviders())
                            .createEnablePhase("enableTasks", (module, holder) -> module.performEnableTasks(this.serviceCollection))
                            .createPreEnablePhase("postenable", holder -> Sponge.getEventManager().post(new BaseModuleEvent.Enabled(this)))
                            .createEnablePhase("tokens", (module, holder) -> module.loadTokens())
                            .createEnablePhase("interceptors", (module, holder) -> module.registerCommandInterceptors())
                            .createEnablePhase("postTasks", (module, holder) -> module.performPostTasks(this.serviceCollection))
                            .build();

            final IConfigurateHelper configurateHelper = this.serviceCollection.configurateHelper();
            this.moduleContainer = db
                    .setConfigurationLoader(builder.setDefaultOptions(configurateHelper.setOptions(builder.getDefaultOptions()).setHeader(he)).build())
                    .setPackageToScan(this.getClass().getPackage().getName() + ".modules")
                    .setLoggerProxy(new NucleusLoggerProxy(this.logger))
                    .setConfigurationOptionsTransformer(x -> configurateHelper.setOptions(x).setHeader(he))
                    .setAllowDisable(false)
                    .setModuleEnabler(enabler)
                    .setRequireModuleDataAnnotation(true)
                    .setNoMergeIfPresent(true)
                    .transformConfig(new AbstractConfigAdapter.Transformation(
                            new Object[] { "admin", "broadcast-message-template" },
                            (inputPath, valueAtPath) -> new Object[] { "notification", "broadcast-message-template" }
                    ))
                    .setModuleConfigurationHeader(m -> {
                            final StringBuilder ssb = new StringBuilder().append(divider).append("\n");
                            final String name = m.getClass().getAnnotation(ModuleData.class).name();
                            final int nameLength = name.length() + 2;
                            final int dashes = (length - nameLength) / 2;
                            ssb.append("|");
                            for (int i = 0; i < dashes; i++) {
                                ssb.append(" ");
                            }

                            ssb.append(" ").append(name).append(" ");
                            for (int i = 0; i < dashes; i++) {
                                ssb.append(" ");
                            }

                            if (length > dashes * 2 + nameLength) {
                                ssb.append(" ");
                            }

                            return ssb.append("|").append("\n").append(divider).toString();
                    })
                    .setModuleConfigSectionName("-modules")
                    .setModuleConfigSectionDescription(messageProvider.getMessageString("config.module-desc"))
                    .setModuleDescriptionHandler(m -> messageProvider.getMessageString("config.module." +
                            m.getAnnotation(ModuleData.class).id().toLowerCase() + ".desc"))
                    .build();

            this.moduleContainer.startDiscover();
            this.serviceCollection.reloadableService().registerEarlyReloadable(serviceCollection -> {
                try {
                    this.moduleContainer.reloadSystemConfig();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (final Exception e) {
            this.isErrored = e;
            this.disable();
            e.printStackTrace();
            return;
        }

        // Load up the general data files now, mods should have registered items by now.
        try {
            // Reloadable so that we can update the serialisers.
            this.moduleContainer.reloadSystemConfig();
        } catch (final Exception e) {
            this.isErrored = e;
            this.disable();
            e.printStackTrace();
            return;
        }

        try {
            Sponge.getEventManager().post(new BaseModuleEvent.AboutToConstructEvent(this));
            this.logger.info(messageProvider.getMessageString("startup.moduleloading", NucleusPluginInfo.NAME));
            this.moduleContainer.loadModules(true);

            final CoreConfig coreConfig = this.moduleContainer.getConfigAdapterForModule(CoreModule.ID, CoreConfigAdapter.class).getNodeOrDefault();

            if (coreConfig.isErrorOnStartup()) {
                throw new IllegalStateException("In main.conf, core.simulate-error-on-startup is set to TRUE. Remove this config entry to allow Nucleus to start. Simulating error and disabling Nucleus.");
            }
            this.serviceCollection.userPreferenceService().postInit();
        } catch (final Throwable construction) {
            this.logger.info(messageProvider.getMessageString("startup.modulenotloaded", NucleusPluginInfo.NAME));
            construction.printStackTrace();
            this.disable();
            this.isErrored = construction;
            return;
        }

        this.logger.info(messageProvider.getMessageString("startup.moduleloaded", NucleusPluginInfo.NAME));
        Sponge.getEventManager().post(new BaseModuleEvent.Complete(this));
        this.logger.info(messageProvider.getMessageString("startup.completeinit", NucleusPluginInfo.NAME));
    }

    @Listener(order = Order.POST)
    public void onInitLate(final GameInitializationEvent event) {
        final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
        if (this.isErrored != null) {
            return;
        }

        this.logger.info(messageProvider.getMessageString("startup.postinit", NucleusPluginInfo.NAME));

        // Actual command registration happens here
        this.serviceCollection.commandMetadataService().completeRegistrationPhase(this.serviceCollection, );
        this.serviceCollection.permissionService().registerDescriptions();

        if (this.docgenOnly) {
            final Path finalPath;
            try {
                final String docgenPath = System.getProperty(DOCGEN_PROPERTY);
                if (docgenPath.isEmpty()) {
                    finalPath = this.dataDir.get();
                } else {
                    final Path path = Sponge.getGame().getGameDirectory().resolve(docgenPath);
                    boolean isOk = path.toAbsolutePath().startsWith(Sponge.getGame().getGameDirectory().toAbsolutePath());
                    isOk &= Files.notExists(path) || Files.isDirectory(path);
                    if (isOk) {
                        Files.createDirectories(path);
                        finalPath = path;
                    } else {
                        finalPath = this.dataDir.get();
                    }
                }
                this.logger.info("Starting generation of documentation, saving files to: {}", finalPath.toString());
                this.serviceCollection.documentationGenerationService().generate(finalPath);
                this.logger.info("Generation is complete. Server will shut down.");
            } catch (final Exception ex) {
                this.logger.error("Could not generate. Server will shut down.");
                ex.printStackTrace();
            }
        }
    }

    @Listener
    public void onPostInit(final GamePostInitializationEvent event) {
        if (this.serviceCollection.moduleDataProvider().getModuleConfig(CoreConfig.class).isGiveDefaultsUserPermissions()) {
            this.logger.info(this.serviceCollection.messageProvider().getMessageString("startup.defaultpermission"));
            this.serviceCollection.permissionService().assignUserRoleToDefault();
        }
    }

    @Listener(order = Order.EARLY)
    public void onGameStartingEarly(final GameStartingServerEvent event) {
        if (this.docgenOnly) {
            return;
        }
        final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
        if (!this.isServer) {
            try {
                this.logger.info(messageProvider.getMessageString("startup.loaddata", NucleusPluginInfo.NAME));
                this.allChange();
            } catch (final Exception e) {
                this.isErrored = e;
                this.disable();
                e.printStackTrace();
            }
        }
    }

    private void allChange() throws Exception {
        this.serviceCollection.storageManager().saveAndInvalidateAllCaches();
        this.resetDataPath();
        final IReloadableService reloadableService = this.serviceCollection.reloadableService();
        reloadableService.fireDataFileReloadables(this.serviceCollection);
        reloadableService.fireReloadables(this.serviceCollection);
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        this.serviceCollection.commandMetadataService().completeRegistrationPhase(this.serviceCollection, event);
    }

    @Listener
    public void onGameStarting(final StartingEngineEvent<Server> event) {
        if (this.docgenOnly) {
            return;
        }
        final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
        if (this.isErrored == null) {
            this.logger.info(messageProvider.getMessageString("startup.gamestart", NucleusPluginInfo.NAME));

            // Load up the general data files now, mods should have registered items by now.
            try {
                this.serviceCollection.reloadableService().fireDataFileReloadables(this.serviceCollection);
//                this.kitDataService.loadInternal();
            } catch (final Exception e) {
                this.isErrored = e;
                this.disable();
                e.printStackTrace();
                return;
            }

            // Start the user cache walk if required, the user storage service is loaded at this point.
            Task.builder().async().execute(() -> this.serviceCollection.userCacheService().startFilewalkIfNeeded()).submit(this);
            this.logger.info(messageProvider.getMessageString("startup.started", NucleusPluginInfo.NAME));
        }
    }

    @Listener(order = Order.PRE)
    public void onGameStarted(final StartedEngineEvent<Server> event) {
        if (this.shutdownAtLoadEnd) {
            Sponge.getServer().shutdown();
            return;
        }
        final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
        if (this.isErrored == null) {
            try {
                this.serviceCollection.getServiceUnchecked(UniqueUserService.class).resetUniqueUserCount();
                this.serviceCollection.getServiceUnchecked(UUIDChangeService.class).setStateAndReload(this.serviceCollection);
                this.serviceCollection.commandMetadataService().activate();

                // Save any additions.
                this.moduleContainer.refreshSystemConfig();
                this.fireReloadables();
            } catch (final Throwable e) {
                this.isErrored = e;
                this.disable();
                this.errorOnStartup();
                return;
            }

            this.hasStarted = true;
            Sponge.getScheduler().createSyncExecutor(this).submit(() -> this.serviceCollection.platformService().setGameStartedTime());

            if (this.serviceCollection.moduleDataProvider().getModuleConfig(CoreConfig.class).isWarningOnStartup()) {
                // What about perms and econ?
                final List<Text> lt = Lists.newArrayList();
                if (this.serviceCollection.permissionService().isOpOnly()) {
                    this.addTri(lt);
                    lt.add(messageProvider.getMessage("standard.line"));
                    lt.add(messageProvider.getMessage("standard.nopermplugin"));
                    lt.add(messageProvider.getMessage("standard.nopermplugin2"));
                }

                if (!Sponge.getServiceManager().isRegistered(EconomyService.class)) {
                    if (lt.isEmpty()) {
                        this.addTri(lt);
                    }

                    lt.add(messageProvider.getMessage("standard.line"));
                    lt.add(messageProvider.getMessage("standard.noeconplugin"));
                    lt.add(messageProvider.getMessage("standard.noeconplugin2"));
                }

                if (!lt.isEmpty()) {
                    lt.add(messageProvider.getMessage("standard.line"));
                    lt.add(messageProvider.getMessage("standard.seesuggested"));
                }

                if (!this.startupMessages.isEmpty()) {
                    if (lt.isEmpty()) {
                        this.addTri(lt);
                    }

                    lt.add(messageProvider.getMessage("standard.line"));
                    lt.addAll(this.startupMessages);
                    this.startupMessages.clear();
                }

                if (!lt.isEmpty()) {
                    lt.add(messageProvider.getMessage("standard.line"));
                    final ConsoleSource c = Sponge.getServer().getConsole();
                    lt.forEach(c::sendMessage);
                }
            }
        }
    }

    @Listener
    public void onServerStop(final GameStoppedServerEvent event) {
        if (this.docgenOnly) {
            return;
        }
        final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
        if (this.hasStarted && this.isErrored == null) {
            this.serviceCollection.platformService().unsetGameStartedTime();
            this.logger.info(messageProvider.getMessageString("startup.stopped", NucleusPluginInfo.NAME));
            this.saveData();
            this.serviceCollection.commandMetadataService().deactivate();
        }
    }

    private void saveData() {
        final IStorageManager ism = this.serviceCollection.storageManager();
        ism.getUserService().ensureSaved();
        ism.getWorldService().ensureSaved();

        if (Sponge.getGame().getState().ordinal() > GameState.SERVER_ABOUT_TO_START.ordinal()) {
            try {
                ism.getGeneralService().ensureSaved();
                this.serviceCollection.userCacheService().save();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void resetDataPath() throws IOException {
        Path path;
        boolean custom = false;
        if (this.dataFileLocation == null) {
            path = this.dataDir.get();
        } else {
            custom = true;
            if (this.dataFileLocation.isAbsolute()) {
                path = this.dataFileLocation;
            } else {
                path = this.dataDir.get().resolve(this.dataFileLocation);
            }

            if (!Files.isDirectory(path)) {
                // warning
                this.logger.error(this.serviceCollection.messageProvider().getMessageString("nucleus.custompath.error",
                        path.toAbsolutePath().toString(),
                        this.dataDir.get().toAbsolutePath().toString()));
                custom = false;
                path = this.dataDir.get();
            }
        }

        final Path currentDataDir = path.resolve("nucleus");
        if (custom) {
            this.logger.info(this.serviceCollection.messageProvider().getMessageString("nucleus.custompath.info",
                    currentDataDir.toAbsolutePath().toString()));
        }
        Files.createDirectories(currentDataDir);
    }

    private void fireReloadables() {
        this.serviceCollection.reloadableService().fireReloadables(this.serviceCollection);
    }

    public DiscoveryModuleHolder<StandardModule, StandardModule> getModuleHolder() {
        return this.moduleContainer;
    }

    private void disable() {
        // Disable everything, just in case. Thanks to pie-flavor: https://forums.spongepowered.org/t/disable-plugin-disable-itself/15831/8
        Sponge.getEventManager().unregisterPluginListeners(this);
        Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
        this.serviceCollection.getService(CommandMetadataService.class).ifPresent(CommandMetadataService::deactivate);

        // Re-register this to warn people about the error.
        Sponge.getEventManager().registerListener(this, GameStartedServerEvent.class, e -> this.errorOnStartup());
    }

    private void errorOnStartup() {
        try {
            Sponge.getServer().setHasWhitelist(this.isServer);
        } catch (final Throwable e) {
            //ignored
        }

        if (this.versionFail != null) {
            Sponge.getServer().getConsole().sendMessages(this.getIncorrectVersion());
        } else {
            Sponge.getServer().getConsole().sendMessages(this.getErrorMessage());
        }

        if (this.shutdownAtLoadEnd) {
            Sponge.getServer().shutdown();
        }
    }

    private List<Text> getIncorrectVersion() {
        final List<Text> messages = Lists.newArrayList();
        messages.add(Text.of(TextColors.RED, "------------------------------"));
        messages.add(Text.of(TextColors.RED, "-   NUCLEUS FAILED TO LOAD   -"));
        messages.add(Text.of(TextColors.RED, "------------------------------"));
        this.addX(messages, 7);
        messages.add(Text.of(TextColors.RED, "------------------------------"));
        messages.add(Text.of(TextColors.RED, "-  INCORRECT SPONGE VERSION  -"));
        messages.add(Text.of(TextColors.RED, "------------------------------"));
        messages.add(Text.EMPTY);
        messages.add(Text.of(TextColors.RED, "You are running "
                + "a mismatched version of Sponge on your server - this version of Nucleus will not run "
                + "upon it."));
        messages.add(Text.of(TextColors.RED, "Nucleus has not started. Update Sponge to the latest version and try again."));
        if (this.isServer) {
            messages.add(Text.of(TextColors.RED,
                    "The server has been automatically whitelisted - this is to protect your server and players if you rely on some of Nucleus' functionality (such as fly states, etc.)"));
        }
        messages.add(Text.of(TextColors.RED, "------------------------------"));
        messages.add(Text.of(TextColors.YELLOW, "Reason: "));
        messages.add(Text.of(TextColors.YELLOW, this.versionFail));
        messages.add(Text.of(TextColors.RED, "------------------------------"));
        messages.add(Text.of(TextColors.YELLOW, "Current Sponge Implementation: ",
                Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName(), ", version ",
                Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse("unknown"), "."));
        return messages;
    }

    private void addTri(final List<Text> messages) {
        messages.add(Text.of(TextColors.YELLOW, "        /\\"));
        messages.add(Text.of(TextColors.YELLOW, "       /  \\"));
        messages.add(Text.of(TextColors.YELLOW, "      / || \\"));
        messages.add(Text.of(TextColors.YELLOW, "     /  ||  \\"));
        messages.add(Text.of(TextColors.YELLOW, "    /   ||   \\"));
        messages.add(Text.of(TextColors.YELLOW, "   /    ||    \\"));
        messages.add(Text.of(TextColors.YELLOW, "  /            \\"));
        messages.add(Text.of(TextColors.YELLOW, " /      **      \\"));
        messages.add(Text.of(TextColors.YELLOW, "------------------"));
    }

    private void addX(final List<Text> messages, final int spacing) {
        final TextComponent space = Text.of(String.join("", Collections.nCopies(spacing, " ")));
        messages.add(Text.of(space, TextColors.RED, "\\              /"));
        messages.add(Text.of(space, TextColors.RED, " \\            /"));
        messages.add(Text.of(space, TextColors.RED, "  \\          /"));
        messages.add(Text.of(space, TextColors.RED, "   \\        /"));
        messages.add(Text.of(space, TextColors.RED, "    \\      /"));
        messages.add(Text.of(space, TextColors.RED, "     \\    /"));
        messages.add(Text.of(space, TextColors.RED, "      \\  /"));
        messages.add(Text.of(space, TextColors.RED, "       \\/"));
        messages.add(Text.of(space, TextColors.RED, "       /\\"));
        messages.add(Text.of(space, TextColors.RED, "      /  \\"));
        messages.add(Text.of(space, TextColors.RED, "     /    \\"));
        messages.add(Text.of(space, TextColors.RED, "    /      \\"));
        messages.add(Text.of(space, TextColors.RED, "   /        \\"));
        messages.add(Text.of(space, TextColors.RED, "  /          \\"));
        messages.add(Text.of(space, TextColors.RED, " /            \\"));
        messages.add(Text.of(space, TextColors.RED, "/              \\"));
    }

    private List<Text> getErrorMessage() {
        final boolean isConfigError =
                this.isErrored != null &&
                this.isErrored instanceof IOException &&
                this.isErrored.getCause() != null &&
                this.isErrored.getCause().getClass().getName().contains(ConfigException.class.getSimpleName());
        final List<Text> messages = Lists.newArrayList();
        messages.add(Text.of(TextColors.RED, "----------------------------"));
        messages.add(Text.of(TextColors.RED, "-  NUCLEUS FAILED TO LOAD  -"));
        messages.add(Text.of(TextColors.RED, "----------------------------"));

        if (isConfigError) {
            messages.add(Text.of(TextColors.RED, "-  ERROR IN CONFIGURATION  -"));
            messages.add(Text.of(TextColors.RED, "----------------------------"));
        }

        this.addX(messages, 5);
        messages.add(Text.of(TextColors.RED, "----------------------------"));

        messages.add(Text.EMPTY);
        if (isConfigError) {
            messages.add(Text.of(TextColors.RED, "One of your configuration files is broken and could not be read."));
            messages.add(Text.EMPTY);
            messages.add(Text.of(TextColors.RED, this.isErrored.getCause().getMessage()));
            messages.add(Text.EMPTY);
            messages.add(Text.of(TextColors.RED, "This is usually because you've added an ID but forgotten to surround the ID with double quotes "
                    + "(\")."));
            messages.add(Text.of(TextColors.RED, "For example, to add minecraft:stone to the config, it should be added as \"minecraft:stone\""));
            messages.add(Text.EMPTY);
            messages.add(Text.of(TextColors.RED, "You will need to fix the configuration file and restart your server."));
        } else {
            messages.add(Text.of(TextColors.RED, "Nucleus encountered an error during server start up and did not enable successfully."));
        }
        messages.add(Text.EMPTY);
        messages.add(Text.of(TextColors.RED, "No commands, listeners or tasks are registered."));
        if (this.isServer) {
            messages.add(Text.of(TextColors.RED,
                    "The server has been automatically whitelisted - this is to protect your server and players if you rely on some of Nucleus' functionality (such as fly states, etc.)"));
        }
        messages.add(Text.of(TextColors.RED, "The error that Nucleus encountered will be reproduced below for your convenience."));

        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        if (this.isErrored == null) {
            messages.add(Text.of(TextColors.YELLOW, "No exception was saved."));
        } else {
            try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
                this.isErrored.printStackTrace(pw);
                pw.flush();
                final String[] stackTrace = sw.toString().split("(\r)?\n");
                for (final String s : stackTrace) {
                    messages.add(Text.of(TextColors.YELLOW, s));
                }
            } catch (final IOException e) {
                this.isErrored.printStackTrace();
            }
        }

        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        messages.add(Text.of(TextColors.RED, "If this error persists, check your configuration files and ensure that you have the latest version of Nucleus which matches the current version of the Sponge API."));
        if (!isConfigError) {
            messages.add(Text.of(TextColors.RED,
                    "If you continue to have this error, please report this error to the Nucleus team at https://github.com/NucleusPowered/Nucleus/issues"));
        }
        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        messages.add(Text.of(TextColors.YELLOW, "Server Information"));
        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        messages.add(Text.of(TextColors.YELLOW, "Nucleus version: " + NucleusPluginInfo.VERSION + ", (Git: " + NucleusPluginInfo.GIT_HASH + ")"));

        final Platform platform = Sponge.getPlatform();
        messages.add(Text.of(TextColors.YELLOW, "Minecraft version: " + platform.getMinecraftVersion().getName()));
        messages.add(Text.of(TextColors.YELLOW, String.format("Sponge Version: %s %s", platform.getContainer(Platform.Component.IMPLEMENTATION).getName(),
                platform.getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse("unknown"))));
        messages.add(Text.of(TextColors.YELLOW, String.format("Sponge API Version: %s %s", platform.getContainer(Platform.Component.API).getName(),
                platform.getContainer(Platform.Component.API).getVersion().orElse("unknown"))));

        messages.add(Text.EMPTY);
        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        messages.add(Text.of(TextColors.YELLOW, "Installed Plugins"));
        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        Sponge.getPluginManager().getPlugins().forEach(x -> messages.add(Text.of(TextColors.YELLOW, x.getName() + " (" + x.getId() + ") version " + x.getVersion().orElse("unknown"))));

        messages.add(Text.EMPTY);
        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        messages.add(Text.of(TextColors.YELLOW, "- END NUCLEUS ERROR REPORT -"));
        messages.add(Text.of(TextColors.YELLOW, "----------------------------"));
        return messages;
    }

}