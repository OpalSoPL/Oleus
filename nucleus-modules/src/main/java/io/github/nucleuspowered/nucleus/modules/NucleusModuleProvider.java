/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
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
import io.github.nucleuspowered.nucleus.modules.fly.FlyModule;
import io.github.nucleuspowered.nucleus.modules.fly.config.FlyConfig;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.FreezePlayerModule;
import io.github.nucleuspowered.nucleus.modules.fun.FunModule;
import io.github.nucleuspowered.nucleus.modules.home.HomeModule;
import io.github.nucleuspowered.nucleus.modules.home.config.HomeConfig;
import io.github.nucleuspowered.nucleus.modules.ignore.IgnoreModule;
import io.github.nucleuspowered.nucleus.modules.info.InfoModule;
import io.github.nucleuspowered.nucleus.modules.info.config.InfoConfig;
import io.github.nucleuspowered.nucleus.modules.inventory.InventoryModule;
import io.github.nucleuspowered.nucleus.modules.invulnerability.InvulnerabilityModule;
import io.github.nucleuspowered.nucleus.modules.item.ItemModule;
import io.github.nucleuspowered.nucleus.modules.item.config.ItemConfig;
import io.github.nucleuspowered.nucleus.modules.jail.JailModule;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jump.JumpModule;
import io.github.nucleuspowered.nucleus.modules.jump.config.JumpConfig;
import io.github.nucleuspowered.nucleus.modules.kick.KickModule;
import io.github.nucleuspowered.nucleus.modules.kick.config.KickConfig;
import io.github.nucleuspowered.nucleus.modules.kit.KitModule;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfig;
import io.github.nucleuspowered.nucleus.modules.mail.MailModule;
import io.github.nucleuspowered.nucleus.modules.message.MessageModule;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfig;
import io.github.nucleuspowered.nucleus.modules.misc.MiscModule;
import io.github.nucleuspowered.nucleus.modules.misc.config.MiscConfig;
import io.github.nucleuspowered.nucleus.modules.mob.MobModule;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.modules.mute.MuteModule;
import io.github.nucleuspowered.nucleus.modules.mute.config.MuteConfig;
import io.github.nucleuspowered.nucleus.modules.nameban.NameBanModule;
import io.github.nucleuspowered.nucleus.modules.nameban.config.NameBanConfig;
import io.github.nucleuspowered.nucleus.modules.nickname.NicknameModule;
import io.github.nucleuspowered.nucleus.modules.nickname.config.NicknameConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class NucleusModuleProvider implements IModuleProvider {

    private final Collection<ModuleContainer> moduleContainers = NucleusModuleProvider.createContainers();

    private static ModuleContainer createContainer(
            final String id,
            final String name,
            final Class<? extends IModule> moduleClass
    ) {
        return new ModuleContainer(id, name, false, moduleClass);
    }

    private static <T> ModuleContainer.Configurable<T> createContainer(
            final String id,
            final String name,
            final Class<? extends IModule.Configurable<T>> moduleClass,
            final Class<T> configurableClass
    ) {
        return new ModuleContainer.Configurable<>(id, name, false, moduleClass, configurableClass);
    }

    private static Collection<ModuleContainer> createContainers() {
        final ArrayList<ModuleContainer> containers = new ArrayList<>();
        containers.add(NucleusModuleProvider.createContainer(AdminModule.ID, "Admin", AdminModule.class, AdminConfig.class));
        containers.add(NucleusModuleProvider.createContainer(AFKModule.ID, "AFK", AFKModule.class, AFKConfig.class));
        containers.add(NucleusModuleProvider.createContainer(BackModule.ID, "Back", BackModule.class, BackConfig.class));
        containers.add(NucleusModuleProvider.createContainer(BanModule.ID, "Ban", BanModule.class, BanConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ChatModule.ID, "Chat", ChatModule.class, ChatConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ChatLoggerModule.ID, "Chat Logger", ChatLoggerModule.class));
        containers.add(NucleusModuleProvider.createContainer(CommandLoggerModule.ID, "Command Logger", CommandLoggerModule.class, CommandLoggerConfig.class));
        containers.add(NucleusModuleProvider.createContainer(CommandSpyModule.ID, "Command Spy", CommandSpyModule.class, CommandSpyConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ConnectionModule.ID, "Connection", ConnectionModule.class, ConnectionConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ConnectionMessagesModule.ID, "Connection Messages", ConnectionMessagesModule.class, ConnectionMessagesConfig.class));
        containers.add(NucleusModuleProvider.createContainer(CraftingGuiModule.ID, "Crafting GUI", CraftingGuiModule.class));
        containers.add(NucleusModuleProvider.createContainer(DeathMessageModule.ID, "Death Message", DeathMessageModule.class, DeathMessageConfig.class));
        containers.add(NucleusModuleProvider.createContainer(EnvironmentModule.ID, "Environment", EnvironmentModule.class, EnvironmentConfig.class));
        containers.add(NucleusModuleProvider.createContainer(ExperienceModule.ID, "Experience", ExperienceModule.class));
        containers.add(NucleusModuleProvider.createContainer(FlyModule.ID, "Flying", FlyModule.class, FlyConfig.class));
        containers.add(NucleusModuleProvider.createContainer(FreezePlayerModule.ID, "Freeze Player", FreezePlayerModule.class));
        containers.add(NucleusModuleProvider.createContainer(FunModule.ID, "Fun", FunModule.class));
        containers.add(NucleusModuleProvider.createContainer(HomeModule.ID, "Home", HomeModule.class, HomeConfig.class));
        containers.add(NucleusModuleProvider.createContainer(IgnoreModule.ID, "Ignore", IgnoreModule.class));
        containers.add(NucleusModuleProvider.createContainer(InfoModule.ID, "Info", InfoModule.class, InfoConfig.class));
        containers.add(NucleusModuleProvider.createContainer(InventoryModule.ID, "Inventory", InventoryModule.class));
        containers.add(NucleusModuleProvider.createContainer(InvulnerabilityModule.ID, "Invulnerability", InvulnerabilityModule.class));
        containers.add(NucleusModuleProvider.createContainer(ItemModule.ID, "Item", ItemModule.class, ItemConfig.class));
        containers.add(NucleusModuleProvider.createContainer(JailModule.ID, "Jail", JailModule.class, JailConfig.class));
        containers.add(NucleusModuleProvider.createContainer(JumpModule.ID, "Jump", JumpModule.class, JumpConfig.class));
        containers.add(NucleusModuleProvider.createContainer(KickModule.ID, "Kick", KickModule.class, KickConfig.class));
        containers.add(NucleusModuleProvider.createContainer(KitModule.ID, "Kit", KitModule.class, KitConfig.class));
        containers.add(NucleusModuleProvider.createContainer(MailModule.ID, "Mail", MailModule.class));
        containers.add(NucleusModuleProvider.createContainer(MessageModule.ID, "Private Message", MessageModule.class, MessageConfig.class));
        containers.add(NucleusModuleProvider.createContainer(MiscModule.ID, "Miscellaneous", MiscModule.class, MiscConfig.class));
        containers.add(NucleusModuleProvider.createContainer(MobModule.ID, "Mob", MobModule.class, MobConfig.class));
        containers.add(NucleusModuleProvider.createContainer(MuteModule.ID, "Mute", MuteModule.class, MuteConfig.class));
        containers.add(NucleusModuleProvider.createContainer(NameBanModule.ID, "Name Ban", NameBanModule.class, NameBanConfig.class));
        containers.add(NucleusModuleProvider.createContainer(NicknameModule.ID, "Nickname", NicknameModule.class, NicknameConfig.class));
        return containers;
    }

    @Override
    public Collection<ModuleContainer> getModules() {
        return Collections.unmodifiableCollection(this.moduleContainers);
    }


}
