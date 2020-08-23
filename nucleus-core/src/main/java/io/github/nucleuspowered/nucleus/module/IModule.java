package io.github.nucleuspowered.nucleus.module;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;

import java.util.Collection;
import java.util.Optional;

public interface IModule {

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
