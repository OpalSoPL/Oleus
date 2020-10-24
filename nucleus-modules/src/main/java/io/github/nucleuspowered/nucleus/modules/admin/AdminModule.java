/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.admin.commands.BlockZapCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.KillCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.KillEntityCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.StopCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.SudoCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.TellPlainCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode.AdventureGamemodeCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode.CreativeGamemodeCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode.GamemodeCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode.GamemodeToggleCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode.SpectatorGamemodeCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode.SurvivalGamemodeCommand;
import io.github.nucleuspowered.nucleus.modules.admin.config.AdminConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AdminModule implements IModule.Configurable<AdminConfig> {

    public final static String ID = "admin";
    public static final String SUDO_LEVEL_KEY = "nucleus.sudo.level";

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        final List<Class<? extends ICommandExecutor>> commands = new ArrayList<>();
        commands.add(BlockZapCommand.class);
        commands.add(KillCommand.class);
        commands.add(KillEntityCommand.class);
        commands.add(StopCommand.class);
        commands.add(SudoCommand.class);
        commands.add(TellPlainCommand.class);
        commands.add(AdventureGamemodeCommand.class);
        commands.add(CreativeGamemodeCommand.class);
        commands.add(GamemodeCommand.class);
        commands.add(GamemodeToggleCommand.class);
        commands.add(SpectatorGamemodeCommand.class);
        commands.add(SurvivalGamemodeCommand.class);
        return Collections.unmodifiableCollection(commands);
    }

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        // no-op
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(AdminPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<AdminConfig> getConfigClass() {
        return AdminConfig.class;
    }

}
