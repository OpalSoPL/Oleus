/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.module.home.NucleusHomeService;
import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.modules.home.config.HomeConfig;
import io.github.nucleuspowered.nucleus.modules.home.services.HomeService;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;

import java.util.Optional;

public class RespawnConditionalListener implements ListenerBase.Conditional {

    private final HomeService homeService;

    @Inject
    public RespawnConditionalListener(final INucleusServiceCollection serviceCollection) {
        this.homeService = serviceCollection.getServiceUnchecked(HomeService.class);
    }

    @Listener
    public void onRespawn(final RespawnPlayerEvent event, @Getter("getPlayer") final ServerPlayer player) {
        final Optional<Home> oh = this.homeService.getHome(player.getUniqueId(), NucleusHomeService.DEFAULT_HOME_NAME);

        if (oh.isPresent()) {
            final Home home = oh.get();
            oh.get().getLocation().ifPresent(x -> {
                event.setToLocation(x);
                event.setToRotation(home.getRotation());
            });
        }

    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(HomeConfig.class).isRespawnAtHome();
    }

}
