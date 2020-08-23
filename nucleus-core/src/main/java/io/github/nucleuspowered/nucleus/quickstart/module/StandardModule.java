/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.quickstart.module;

import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.Constants;
import io.github.nucleuspowered.nucleus.annotationprocessor.Store;
import io.github.nucleuspowered.nucleus.quickstart.annotation.RequireExistenceOf;
import io.github.nucleuspowered.nucleus.quickstart.annotation.RequiresPlatform;
import io.github.nucleuspowered.nucleus.quickstart.annotation.ServerOnly;
import io.github.nucleuspowered.nucleus.quickstart.annotation.SkipOnError;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandInterceptor;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.registry.NucleusRegistryModule;
import io.github.nucleuspowered.nucleus.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.ICommandMetadataService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import uk.co.drnaylor.quickstart.Module;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter;
import uk.co.drnaylor.quickstart.exceptions.MissingDependencyException;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Store(isRoot = true)
public abstract class StandardModule implements Module {

    private final String moduleId;
    private final String moduleName;
    protected final INucleusServiceCollection serviceCollection;
    private final Supplier<DiscoveryModuleHolder<?, ?>> moduleHolderSupplier;
    private final Logger logger;
    private String packageName;
    @Nullable private Map<String, List<String>> objectTypesToClassListMap;

    @Inject
    public StandardModule(final Supplier<DiscoveryModuleHolder<?, ?>> moduleHolder, final INucleusServiceCollection collection) {
        final ModuleData md = this.getClass().getAnnotation(ModuleData.class);
        this.moduleId = md.id();
        this.moduleName = md.name();
        this.serviceCollection = collection;
        this.moduleHolderSupplier = moduleHolder;
        this.logger = collection.logger();
    }

    protected final INucleusServiceCollection getServiceCollection() {
        return this.serviceCollection;
    }

    public void init(final Map<String, List<String>> m) {
        this.objectTypesToClassListMap = m;
    }

    @Override
    public final void checkExternalDependencies() throws MissingDependencyException {
        if (this.getClass().isAnnotationPresent(ServerOnly.class) && !this.serviceCollection.platformService().isServer()) {
            throw new MissingDependencyException("This module is server only and will not be loaded.");
        }
    }

    protected Map<String, PlaceholderParser> tokensToRegister() {
        return ImmutableMap.of();
    }

    /**
     * Non-configurable module, no configuration to register.
     *
     * @return {@link Optional#empty()}
     */
    @Override
    public Optional<AbstractConfigAdapter<?>> getConfigAdapter() {
        return Optional.empty();
    }

    public final void loadServices() throws Exception {
        final Set<Class<? extends ServiceBase>> servicesToLoad;
        if (this.objectTypesToClassListMap != null) {
            servicesToLoad = this.getClassesFromList(Constants.SERVICE);
        } else {
            servicesToLoad = this.getStreamForModule(ServiceBase.class).collect(Collectors.toSet());
        }

        for (final Class<? extends ServiceBase> serviceClass : servicesToLoad) {
            this.registerService(serviceClass);
        }
    }

    private void registerReloadable(final Object instance) {
        if (instance instanceof IReloadableService.Reloadable) {
            final IReloadableService.Reloadable reloadable = (IReloadableService.Reloadable) instance;
            this.serviceCollection.reloadableService().registerReloadable(reloadable);
            reloadable.onReload(this.serviceCollection);
        }
    }

    private <T extends ServiceBase> void registerService(final Class<T> serviceClass) {
        final T serviceImpl = this.getInstance(serviceClass);
        if (serviceImpl == null) {
            final String error = "ERROR: Cannot instantiate " + serviceClass.getName();
            this.logger.error(error);
            throw new IllegalStateException(error);
        }

        final APIService apiService = serviceClass.getAnnotation(APIService.class);
        if (apiService != null) {
            final Class<?> apiInterface = apiService.value();
            if (apiInterface.isInstance(serviceImpl)) {
                // OK
                this.register((Class) apiInterface, serviceClass, serviceImpl);
            } else {
                final String error = "ERROR: " + apiInterface.getName() + " does not inherit from " + serviceClass.getName();
                this.logger.error(error);
                throw new IllegalStateException(error);
            }
        } else {
            this.register(serviceClass, serviceImpl);
        }

        this.registerReloadable(serviceImpl);

        if (serviceImpl instanceof IReloadableService.DataLocationReloadable) {
            // don't do anything let.
            this.serviceCollection.reloadableService().registerDataFileReloadable((IReloadableService.DataLocationReloadable) serviceImpl);
        }

        if (serviceImpl instanceof ContextCalculator) {
            try {
                // boolean matches(Context context, T calculable);
                serviceImpl.getClass().getMethod("matches", Context.class, Subject.class);

                // register it
                //noinspection unchecked
                this.serviceCollection.permissionService().registerContextCalculator((ContextCalculator<Subject>) serviceImpl);
            } catch (final NoSuchMethodException e) {
                // ignored
            }
        }
    }

    public void registerCommandInterceptors() {
        final Set<Class<? extends ICommandInterceptor>> interceptors;
        if (this.objectTypesToClassListMap != null) {
            interceptors = this.getClassesFromList(Constants.INTERCEPTOR);
        } else {
            interceptors = this.getStreamForModule(ICommandInterceptor.class).collect(Collectors.toSet());
        }

        if (!interceptors.isEmpty()) {
            // for each annotation, attempt to register the service.
            for (final Class<? extends ICommandInterceptor> service : interceptors) {

                // create the impl
                final ICommandInterceptor impl;
                try {
                    impl = service.newInstance();
                } catch (final InstantiationException | IllegalAccessException e) {
                    final String error = "ERROR: Cannot instantiate ICommandInterceptor " + service.getName();
                    this.serviceCollection.logger().error(error);
                    throw new IllegalStateException(error, e);
                }

                this.registerReloadable(impl);

                // hahahaha, no
                this.serviceCollection.commandMetadataService().registerInterceptor(impl);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final void loadCommands() {

        final Set<Class<? extends ICommandExecutor>> cmds;
        if (this.objectTypesToClassListMap != null) {
            cmds = this.getClassesFromList(Constants.COMMAND);
        } else {
            cmds = this.performFilter(this.getStreamForModule(ICommandExecutor.class)
                    .map(x -> (Class<? extends ICommandExecutor>) x))
                    .collect(Collectors.toSet());
        }

        final ICommandMetadataService metadataService = this.serviceCollection.commandMetadataService();
        for (final Class<? extends ICommandExecutor> command : cmds) {
            final Command rc = command.getAnnotation(Command.class);
            if (rc != null) {
                // then we should add it.
                metadataService.registerCommand(
                    this.moduleId,
                    this.moduleName,
                    rc,
                    command,
                );
            }
        }

        // Construction happens later in a pre step in QSML
    }

    public final void prepareAliasedCommands() {
        this.serviceCollection.commandMetadataService().addMapping(this.remapCommand());
    }

    private Stream<Class<? extends ICommandExecutor>> performFilter(final Stream<Class<? extends ICommandExecutor>> stream) {
        return stream.filter(x -> x.isAnnotationPresent(Command.class));
    }

    public final void loadEvents() {
        final Set<Class<? extends ListenerBase>> listenersToLoad;
        if (this.objectTypesToClassListMap != null) {
            listenersToLoad = this.getClassesFromList(Constants.LISTENER);
        } else {
            listenersToLoad = this.getStreamForModule(ListenerBase.class).collect(Collectors.toSet());
        }

        listenersToLoad.stream().map(x -> this.getInstance(x, true)).filter(Objects::nonNull).forEach(c -> {
            if (c instanceof ListenerBase.Conditional) {
                // Add reloadable to load in the listener dynamically if required.
                final IReloadableService.Reloadable tae = serviceCollection -> {
                    Sponge.getEventManager().unregisterListeners(c);
                    if (c instanceof IReloadableService.Reloadable) {
                        ((IReloadableService.Reloadable) c).onReload(serviceCollection);
                    }

                    if (((ListenerBase.Conditional) c).shouldEnable(serviceCollection)) {
                        Sponge.getEventManager().registerListeners(serviceCollection.pluginContainer(), c);
                    }
                };

                this.serviceCollection.reloadableService().registerReloadable(tae);
                try {
                    tae.onReload(this.serviceCollection);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else if (c instanceof IReloadableService.Reloadable) {
                this.serviceCollection.reloadableService().registerReloadable((IReloadableService.Reloadable) c);
                Sponge.getEventManager().registerListeners(this.serviceCollection.pluginContainer(), c);
            } else {
                Sponge.getEventManager().registerListeners(this.serviceCollection.pluginContainer(), c);
            }
        });
    }

    public final void loadRunnables() {
        final Set<Class<? extends TaskBase>> tasksToLoad;
        if (this.objectTypesToClassListMap != null) {
            tasksToLoad = this.getClassesFromList(Constants.RUNNABLE);
        } else {
            tasksToLoad = this.getStreamForModule(TaskBase.class).collect(Collectors.toSet());
        }

        tasksToLoad.stream().map(this::getInstance).filter(Objects::nonNull).forEach(c -> {
            final Task.Builder tb = Sponge.getScheduler().createTaskBuilder().interval(c.interval().toMillis(), TimeUnit.MILLISECONDS);
            if (this.serviceCollection.platformService().isServer()) {
                tb.execute(c);
            } else {
                tb.execute(t -> {
                    if (Sponge.getGame().isServerAvailable()) {
                        c.accept(t);
                    }
                });
            }

            if (c.isAsync()) {
                tb.async();
            }

            tb.submit(this.serviceCollection.pluginContainer());

            if (c instanceof IReloadableService.Reloadable) {
                this.serviceCollection.reloadableService().registerReloadable((IReloadableService.Reloadable) c);
                try {
                    ((IReloadableService.Reloadable) c).onReload(this.serviceCollection);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public final void loadTokens() {
        final Map<String, PlaceholderParser> map = this.tokensToRegister();
        if (!map.isEmpty()) {
            map.forEach((k, t) -> {
                try {
                    this.serviceCollection.placeholderService().registerToken(k, t);
                } catch (final Exception e) {
                    this.serviceCollection.logger().warn("Could not register nucleus token identifier " + k, e);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public final void loadRegistries() {
        final Set<Class<? extends NucleusRegistryModule>> registries;
        if (this.objectTypesToClassListMap != null) {
            registries = this.getClassesFromList(Constants.REGISTRY);
        } else {
            registries = this.getStreamForModule(NucleusRegistryModule.class).collect(Collectors.toSet());
        }

        for (final Class<? extends NucleusRegistryModule> r : registries) {
            final NucleusRegistryModule<?> instance = this.getInstance(r);
            try {
                instance.registerDefaults();
            } catch (final Exception e) {
                this.serviceCollection.logger().error("Could not register registry " + r.getName(), e);
            }
        }
    }

    public final void loadInfoProviders() {
        final Set<Class<? extends NucleusProvider>> registries;
        if (this.objectTypesToClassListMap != null) {
            registries = this.getClassesFromList(Constants.PLAYER_INFO);
        } else {
            registries = this.getStreamForModule(NucleusProvider.class).collect(Collectors.toSet());
        }

        for (final Class<? extends NucleusProvider> r : registries) {
            final NucleusProvider instance = this.getInstance(r);
            this.serviceCollection.playerInformationService().registerProvider(instance);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Stream<Class<? extends T>> getStreamForModule(final Class<T> assignableClass) {
        return this.moduleHolderSupplier.get()
                .getLoadedClasses()
                .stream()
                .filter(assignableClass::isAssignableFrom)
                .filter(x -> x.getPackage().getName().startsWith(this.packageName))
                .filter(x -> !Modifier.isAbstract(x.getModifiers()) && !Modifier.isInterface(x.getModifiers()))
                .filter(this::checkPlatform)
                .map(x -> (Class<? extends T>)x);
    }

    public void performPreTasks(final INucleusServiceCollection serviceCollection) throws Exception { }

    public void performEnableTasks(final INucleusServiceCollection serviceCollection) throws Exception { }

    public void performPostTasks(final INucleusServiceCollection serviceCollection) { }

    public void configTasks() {

    }

    protected ImmutableMap<String, String> remapCommand() {
        return ImmutableMap.of();
    }

    private <T> T getInstance(final Class<T> clazz) {
        return this.getInstance(clazz, false);
    }

    private <T> T getInstance(final Class<T> clazz, final boolean checkMethods) {
        try {
            final RequireExistenceOf[] v = clazz.getAnnotationsByType(RequireExistenceOf.class);
            if (v.length > 0) {
                try {
                    for (final RequireExistenceOf r : v) {
                        final String toFind = r.value();
                        final String[] a;
                        if (toFind.contains("#")) {
                            a = toFind.split("#", 2);
                        } else {
                            a = new String[]{toFind};
                        }

                        // Check the class.
                        final Class<?> c = Class.forName(a[0]);
                        if (a.length == 2) {
                            // Check the method
                            final Method[] methods = c.getDeclaredMethods();
                            boolean methodFound = false;
                            for (final Method m : methods) {
                                if (m.getName().equals(a[1])) {
                                    methodFound = true;
                                    break;
                                }
                            }

                            if (!methodFound) {
                                if (r.showError()) {
                                    throw new RuntimeException();
                                }

                                return null;
                            }
                        }
                    }
                } catch (final ClassNotFoundException | RuntimeException | NoClassDefFoundError e) {
                    this.serviceCollection.logger().warn(this.serviceCollection.messageProvider().getMessageString("startup.injectablenotloaded", clazz.getName()));
                    return null;
                }
            }

            if (checkMethods) {
                // This checks all the methods to ensure the classes in question exist.
                clazz.getDeclaredMethods();
            }

            return this.construct(clazz);

        // I can't believe I have to do this...
        } catch (final Exception | NoClassDefFoundError e) {
            if (clazz.isAnnotationPresent(SkipOnError.class)) {
                this.serviceCollection.logger()
                        .warn(this.serviceCollection.messageProvider().getMessageString("startup.injectablenotloaded", clazz.getName()));
                return null;
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private <T extends Class<?>> Optional<T> checkPlatformOpt(final T clazz) {
        if (this.checkPlatform(clazz)) {
            return Optional.of(clazz);
        }

        return Optional.empty();
    }

    private <T extends Class<?>> boolean checkPlatform(final T clazz) {
        if (clazz.isAnnotationPresent(RequiresPlatform.class)) {
            final String platformId = Sponge.getPlatform().getContainer(Platform.Component.GAME).getId();
            final boolean loadable = Arrays.stream(clazz.getAnnotation(RequiresPlatform.class).value()).anyMatch(platformId::equalsIgnoreCase);
            if (!loadable) {
                this.serviceCollection.logger().warn("Not loading /" + clazz.getSimpleName() + ": platform " + platformId + " is not supported.");
                return false;
            }
        }

        return true;
    }

    protected final <I, S extends I> void register(final Class<S> impl) {
        this.register(impl, this.serviceCollection.injector().getInstance(impl));
    }

    protected final <I, S extends I> void register(final Class<I> api, final Class<S> impl) {
        final S object = this.serviceCollection.injector().getInstance(impl);
        Sponge.getServiceManager().setProvider(this.serviceCollection.pluginContainer(), api, object);
        this.register(api, object);
        this.register(impl, object);
    }

    protected final <I, S extends I> void register(final Class<? super S> impl, final S object) {
        this.serviceCollection.registerService(impl, object, false);
    }

    protected final <I, S extends I> void register(final Class<I> internalApi, final Class<S> impl, final S object, final boolean remap) {
        this.register(impl, object);
        this.serviceCollection.registerService(internalApi, object, remap);
    }

    protected final <I, S extends I> void register(final Class<I> api, final Class<S> impl, final S object) {
        Sponge.getServiceManager().setProvider(this.serviceCollection.pluginContainer(), api, object);
        this.serviceCollection.registerService(api, object, false);
        this.register(impl, object);
    }

    private <T> Set<Class<? extends T>> getClassesFromList(final String key) {
        final List<String> list = this.objectTypesToClassListMap.get(key);
        if (list == null) {
            return new HashSet<>();
        }

        final Set<Class<? extends T>> classes = new HashSet<>();
        for (final String s : list) {
            try {
                this.checkPlatformOpt((Class<? extends T>) Class.forName(s)).ifPresent(classes::add);
            } catch (final ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return classes;
    }

    private <T> T construct(final Class<T> cls) throws Exception {
        try {
            final Constructor<T> c = cls.getDeclaredConstructor(INucleusServiceCollection.class);
            c.setAccessible(true);
            return c.newInstance(this.serviceCollection);
        } catch (final NoSuchMethodException e) {
            // nope, do we have parameterless?
            try {
                final Constructor<T> c = cls.getDeclaredConstructor();
                c.setAccessible(true);
                return c.newInstance();
            } catch (final NoSuchMethodException ex) {
                // nope
                return this.serviceCollection.injector().getInstance(cls);
            }
        }
    }

    public void registerPermissions() {
        final IPermissionService permissionService = this.serviceCollection.permissionService();
        for (final Class<?> c : this.getClassesFromList(Constants.PERMISSIONS)) {
            for (final Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(PermissionMetadata.class)
                        && String.class.isAssignableFrom(field.getType())
                        && (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                    try {
                        permissionService.register(
                                field.get(null).toString(),
                                field.getAnnotation(PermissionMetadata.class),
                                this.moduleId
                        );
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
