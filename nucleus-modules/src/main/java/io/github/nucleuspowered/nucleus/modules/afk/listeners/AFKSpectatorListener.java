/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.module.afk.event.NucleusAFKEvent;
import io.github.nucleuspowered.nucleus.modules.afk.AFKPermissions;
import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;

public class AFKSpectatorListener implements ListenerBase.Conditional {

    private final IPermissionService permissionService;

    @Inject
    public AFKSpectatorListener(final IPermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @Listener
    public void onAfk(final NucleusAFKEvent event, @Getter("getTargetPlayer") final ServerPlayer player) {
        if (player.gameMode().get().equals(GameModes.SPECTATOR)) {
            if (event.getAudience().filter(x -> Sponge.getSystemSubject().equals(x)).isPresent()) {
                event.setAudience(this.permissionService.permissionMessageChannel(AFKPermissions.AFK_NOTIFY));
                event.setMessage(Component.text("[Spectator] " + event.getMessage(), NamedTextColor.RED));
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void onAfk(final NucleusAFKEvent.Kick event, @Getter("getTargetPlayer") final ServerPlayer player) {
        if (player.gameMode().get().equals(GameModes.SPECTATOR)) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(AFKConfig.class).isDisableInSpectatorMode();
    }
}
