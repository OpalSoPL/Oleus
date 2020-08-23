package io.github.nucleuspowered.nucleus.modules;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.module.IModuleProvider;
import io.github.nucleuspowered.nucleus.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.modules.admin.AdminModule;
import io.github.nucleuspowered.nucleus.modules.admin.config.AdminConfig;
import io.github.nucleuspowered.nucleus.modules.afk.AFKModule;
import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfig;
import io.github.nucleuspowered.nucleus.modules.back.BackModule;
import io.github.nucleuspowered.nucleus.modules.back.config.BackConfig;
import io.github.nucleuspowered.nucleus.modules.ban.BanModule;
import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfig;
import io.github.nucleuspowered.nucleus.modules.chat.ChatModule;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfig;
import io.github.nucleuspowered.nucleus.modules.chatlogger.ChatLoggerModule;
import io.github.nucleuspowered.nucleus.modules.commandlogger.CommandLoggerModule;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfig;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyModule;
import io.github.nucleuspowered.nucleus.modules.commandspy.commands.CommandSpyCommand;
import io.github.nucleuspowered.nucleus.modules.commandspy.config.CommandSpyConfig;
import io.github.nucleuspowered.nucleus.modules.connection.ConnectionModule;
import io.github.nucleuspowered.nucleus.modules.connection.config.ConnectionConfig;
import io.github.nucleuspowered.nucleus.modules.connectionmessages.ConnectionMessagesModule;
import io.github.nucleuspowered.nucleus.modules.connectionmessages.config.ConnectionMessagesConfig;
import io.github.nucleuspowered.nucleus.modules.craftinggui.CraftingGuiModule;
import io.github.nucleuspowered.nucleus.modules.deathmessage.DeathMessageModule;
import io.github.nucleuspowered.nucleus.modules.deathmessage.config.DeathMessageConfig;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentModule;
import io.github.nucleuspowered.nucleus.modules.environment.config.EnvironmentConfig;
import io.github.nucleuspowered.nucleus.modules.experience.ExperienceModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class NucleusModuleProvider implements IModuleProvider {

    private final Collection<ModuleContainer> moduleContainers = NucleusModuleProvider.createContainers();

    private static ModuleContainer createContainer(
            final String id,
            final Class<? extends IModule> moduleClass
    ) {
        return new ModuleContainer(id, false, moduleClass);
    }

    private static <T> ModuleContainer.Configurable<T> createContainer(
            final String id,
            final Class<? extends IModule> moduleClass,
            final Class<T> configurableClass
    ) {
        return new ModuleContainer.Configurable<>(id, false, moduleClass, configurableClass);
    }

    private static Collection<ModuleContainer> createContainers() {
        final ArrayList<ModuleContainer> containers = new ArrayList<>();
        containers.add(NucleusModuleProvider.createContainer(AdminModule.ID, AdminModule.class, AdminConfig.class));
        containers.add(NucleusModuleProvider.createContainer(AFKModule.ID, AFKModule.class, AFKConfig.class));
        containers.add(NucleusModuleProvider.createContainer(BackModule.ID, BackModule.class, BackConfig.class));
        containers.add(NucleusModuleProvider.createContainer(BanModule.ID, BanModule.class, BanConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ChatModule.ID, ChatModule.class, ChatConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ChatLoggerModule.ID, ChatLoggerModule.class));
        containers.add(NucleusModuleProvider.createContainer(CommandLoggerModule.ID, CommandLoggerModule.class, CommandLoggerConfig.class));
        containers.add(NucleusModuleProvider.createContainer(CommandSpyModule.ID, CommandSpyModule.class, CommandSpyConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ConnectionModule.ID, ConnectionModule.class, ConnectionConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ConnectionMessagesModule.ID, ConnectionMessagesModule.class, ConnectionMessagesConfig.class));
        containers.add(NucleusModuleProvider.createContainer(CraftingGuiModule.ID, CraftingGuiModule.class));
        containers.add(NucleusModuleProvider.createContainer(DeathMessageModule.ID, DeathMessageModule.class, DeathMessageConfig.class));
        containers.add(NucleusModuleProvider.createContainer(EnvironmentModule.ID, EnvironmentModule.class, EnvironmentConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ExperienceModule.ID, ExperienceModule.class));
        containers.add(NucleusModuleProvider.createContainer("fly", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("freeze-subject", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("fun", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("environment", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("environment", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("environment", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("environment", EnvironmentModule.class));
        containers.add(NucleusModuleProvider.createContainer("environment", EnvironmentModule.class));

        return containers;
    }

    @Override
    public Collection<ModuleContainer> getModules() {
        return Collections.unmodifiableCollection(this.moduleContainers);
    }


}
