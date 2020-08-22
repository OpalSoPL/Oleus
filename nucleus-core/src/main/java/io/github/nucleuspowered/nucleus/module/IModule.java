package io.github.nucleuspowered.nucleus.module;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;

import java.util.Collection;
import java.util.Optional;

public interface IModule {

    Collection<ICommandExecutor<?>> getCommands();

    Optional<Object> getPermissions();

    Collection<ListenerBase> getListeners();

    Collection<TaskBase> getTasks();



}
