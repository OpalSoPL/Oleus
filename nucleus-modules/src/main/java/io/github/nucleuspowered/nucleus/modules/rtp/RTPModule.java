/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp;

import io.github.nucleuspowered.nucleus.api.module.rtp.kernel.RTPKernel;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.rtp.commands.RandomTeleportCommand;
import io.github.nucleuspowered.nucleus.modules.rtp.config.RTPConfig;
import io.github.nucleuspowered.nucleus.modules.rtp.kernels.AroundPlayerAndSurfaceKernel;
import io.github.nucleuspowered.nucleus.modules.rtp.kernels.AroundPlayerKernel;
import io.github.nucleuspowered.nucleus.modules.rtp.kernels.DefaultKernel;
import io.github.nucleuspowered.nucleus.modules.rtp.kernels.SurfaceKernel;
import io.github.nucleuspowered.nucleus.modules.rtp.services.RTPService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;
import org.spongepowered.api.event.lifecycle.RegisterCatalogRegistryEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class RTPModule implements IModule.Configurable<RTPConfig> {

    public static final String ID = "rtp";
    private static final ResourceKey CATALOG_KEY = ResourceKey.of("nucleus", "rtp_kernel");

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(RTPService.class, new RTPService(serviceCollection), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singletonList(RandomTeleportCommand.class);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(RTPPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Class<RTPConfig> getConfigClass() {
        return RTPConfig.class;
    }

    @Listener
    public void onCatalogTypeRegistration(final RegisterCatalogRegistryEvent event) {
        event.register(RTPKernel.class, RTPModule.CATALOG_KEY);
    }

    @Listener
    public void onRTPKernelRegistration(final RegisterCatalogEvent<RTPKernel> event) {
        event.register(new DefaultKernel());
        event.register(new SurfaceKernel());
        event.register(new AroundPlayerKernel());
        event.register(new AroundPlayerAndSurfaceKernel());
    }

}
