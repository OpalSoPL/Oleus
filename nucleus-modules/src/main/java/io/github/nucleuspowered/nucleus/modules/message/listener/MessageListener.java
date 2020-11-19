package io.github.nucleuspowered.nucleus.modules.message.listener;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public final class MessageListener implements ListenerBase {

    private final MessageHandler messageHandler;

    @Inject
    public MessageListener(final INucleusServiceCollection serviceCollection) {
        this.messageHandler = serviceCollection.getServiceUnchecked(MessageHandler.class);
    }

    @Listener(order = Order.LAST)
    public void onPlayerJoin(final ServerSideConnectionEvent.Join event, @Getter("getPlayer") final ServerPlayer serverPlayer) {
        this.messageHandler.addPlayer(serverPlayer.getUniqueId());
    }

    @Listener(order = Order.LAST)
    public void onPlayerQuit(final ServerSideConnectionEvent.Disconnect event, @Getter("getPlayer") final ServerPlayer serverPlayer) {
        this.messageHandler.removePlayer(serverPlayer.getUniqueId());
    }

}
