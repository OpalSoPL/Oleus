/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.freezeplayer;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.commands.FreezePlayerCommand;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.infoprovider.FreezeInfoProvider;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.listeners.FreezePlayerListener;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.services.FreezePlayerService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.playerinformation.NucleusProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderParser;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class FreezePlayerModule implements IModule {

    public static final String ID = "freeze-subject";
    public static final Component FROZEN_TAG = Component.text("[Frozen]", NamedTextColor.GRAY);

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        final FreezePlayerService service = new FreezePlayerService(serviceCollection);
        serviceCollection.registerService(FreezePlayerService.class, service, false);
        serviceCollection.placeholderService().registerToken(
                "frozen",
                PlaceholderParser.builder()
                        .parser(p -> {
                            if (p.associatedObject().filter(x -> x instanceof ServerPlayer)
                                    .map(x -> service.isFrozen(((ServerPlayer) x).uniqueId()))
                                    .orElse(false)) {
                                return FreezePlayerModule.FROZEN_TAG;
                            }
                            return Component.empty();
                        }).build()
        );
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singleton(FreezePlayerCommand.class);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(FreezePlayerPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(FreezePlayerListener.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    @Override
    public Optional<NucleusProvider> getInfoProvider() {
        return Optional.of(new FreezeInfoProvider());
    }
}
