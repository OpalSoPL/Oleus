/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.sign.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.sign.SignPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.spongepowered.api.data.value.ListValue;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.entity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class SignListener implements ListenerBase {

    private final IPermissionService permissionService;

    @Inject
    public SignListener(final INucleusServiceCollection serviceCollection) {
        this.permissionService = serviceCollection.permissionService();
    }

    @Listener(order = Order.EARLY)
    public void onPlayerChangeSign(final ChangeSignEvent event, @Root final ServerPlayer player) {
        if (this.permissionService.hasPermission(player, SignPermissions.SIGN_FORMATTING)) {
            final ListValue.Mutable<Component> signText = event.text();
            for (int i = 0; i < signText.size(); i++) {
                signText.set(i, LegacyComponentSerializer.legacyAmpersand().deserialize(
                        PlainComponentSerializer.plain().serialize(signText.get(i))));
            }
        }
    }

}
