/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.afk.commands.AFKCommand;
import io.github.nucleuspowered.nucleus.modules.afk.commands.AFKKickCommand;
import io.github.nucleuspowered.nucleus.modules.afk.commands.AFKRefresh;
import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfig;
import io.github.nucleuspowered.nucleus.modules.afk.infoprovider.AFKInfoProvider;
import io.github.nucleuspowered.nucleus.modules.afk.interceptors.AFKCommandInterceptor;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKChatListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKCommandListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKFullMoveListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKInteractListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKMoveOnlyListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKRotationOnlyListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.AFKSpectatorListener;
import io.github.nucleuspowered.nucleus.modules.afk.listeners.BasicAFKListener;
import io.github.nucleuspowered.nucleus.modules.afk.runnables.AFKRefreshPermsTask;
import io.github.nucleuspowered.nucleus.modules.afk.runnables.AFKTask;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandInterceptor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AFKModule implements IModule.Configurable<AFKConfig> {

    public static final String ID = "afk";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(AFKHandler.class, new AFKHandler(serviceCollection), false);
        serviceCollection.placeholderService().registerToken(
                "afk",
                PlaceholderParser.builder()
                        .key(ResourceKey.of("nucleus", "afk"))
                        .parser(p -> {
                            if (p.getAssociatedObject().filter(x -> x instanceof ServerPlayer)
                                    .map(x -> serviceCollection.getServiceUnchecked(AFKHandler.class).isAFK(((ServerPlayer) x).getUniqueId()))
                                    .orElse(false)) {
                                return Component.text("[AFK]", NamedTextColor.GRAY);
                            }
                            return Component.empty();
                        }).build()
        );
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        final List<Class<? extends ICommandExecutor>> commands = new ArrayList<>();
        commands.add(AFKCommand.class);
        commands.add(AFKKickCommand.class);
        commands.add(AFKRefresh.class);
        return Collections.unmodifiableCollection(commands);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(AFKPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        final List<Class<? extends ListenerBase>> listeners = new ArrayList<>();
        listeners.add(AFKChatListener.class);
        listeners.add(AFKCommandListener.class);
        listeners.add(AFKFullMoveListener.class);
        listeners.add(AFKInteractListener.class);
        listeners.add(AFKMoveOnlyListener.class);
        listeners.add(AFKRotationOnlyListener.class);
        listeners.add(AFKSpectatorListener.class);
        listeners.add(BasicAFKListener.class);
        return Collections.unmodifiableCollection(listeners);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        final List<Class<? extends TaskBase>> tasks = new ArrayList<>();
        tasks.add(AFKRefreshPermsTask.class);
        tasks.add(AFKTask.class);
        return Collections.unmodifiableCollection(tasks);
    }

    @Override
    public Class<AFKConfig> getConfigClass() {
        return AFKConfig.class;
    }

    @Override
    public Collection<ICommandInterceptor> getCommandInterceptors() {
        return Collections.singleton(new AFKCommandInterceptor());
    }

    @Override
    public Optional<NucleusProvider> getInfoProvider() {
        return Optional.of(new AFKInfoProvider());
    }
}
