package io.github.nucleuspowered.nucleus.module;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandInterceptor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public interface IModule {

    void init();

    default Collection<ICommandInterceptor> getCommandInterceptors() {
        return Collections.emptyList();
    }

    Collection<Class<? extends ICommandExecutor>> getCommands();

    Optional<Class<?>> getPermissions();

    Collection<ListenerBase> getListeners();

    Collection<TaskBase> getTasks();

    default Optional<NucleusProvider> getInfoProvider() {
        return Optional.empty();
    }

    interface Configurable<T> extends IModule {

        Class<T> getConfigClass();

    }

}
