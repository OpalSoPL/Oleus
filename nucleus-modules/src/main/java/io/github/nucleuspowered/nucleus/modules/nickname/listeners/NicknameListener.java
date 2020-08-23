/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.listeners;

import io.github.nucleuspowered.nucleus.modules.nickname.services.NicknameService;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import com.google.inject.Inject;

public class NicknameListener implements ListenerBase {

    private final NicknameService nicknameService;

    @Inject
    public NicknameListener(final INucleusServiceCollection serviceCollection) {
        this.nicknameService = serviceCollection.getServiceUnchecked(NicknameService.class);
    }

    @Listener(order = Order.FIRST)
    public void onPlayerJoin(final ClientConnectionEvent.Join event, @Root final Player player) {
        final Optional<Text> nickname = this.nicknameService.getNickname(player);
        this.nicknameService.markRead(player.getUniqueId());
        nickname.ifPresent(text -> {
            this.nicknameService.updateCache(player.getUniqueId(), text);
        });
        player.offer(
                Keys.DISPLAY_NAME,
                nickname.orElseGet(() -> Text.of(player.getName())));
    }

    @Listener(order = Order.LAST)
    public void onPlayerQuit(final ClientConnectionEvent.Disconnect event, @Root final Player player) {
        this.nicknameService.removeFromCache(player.getUniqueId());
    }

}
