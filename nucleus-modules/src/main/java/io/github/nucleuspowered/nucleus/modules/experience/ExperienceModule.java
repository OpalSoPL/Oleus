/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.experience;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.experience.commands.ExperienceCommand;
import io.github.nucleuspowered.nucleus.modules.experience.commands.GiveExperience;
import io.github.nucleuspowered.nucleus.modules.experience.commands.SetExperience;
import io.github.nucleuspowered.nucleus.modules.experience.commands.TakeExperience;
import io.github.nucleuspowered.nucleus.modules.experience.listener.ExperienceListener;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ExperienceModule implements IModule {

    public static final String ID = "experience";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                ExperienceCommand.class,
                GiveExperience.class,
                SetExperience.class,
                TakeExperience.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(ExperiencePermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(ExperienceListener.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

}
