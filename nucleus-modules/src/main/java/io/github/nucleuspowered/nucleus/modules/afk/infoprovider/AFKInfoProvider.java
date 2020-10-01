/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.infoprovider;

import io.github.nucleuspowered.nucleus.modules.afk.AFKPermissions;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.UUID;

public class AFKInfoProvider implements NucleusProvider {

    @Override
    public String getCategory() {
        return "afk";
    }

    @Override
    public Optional<Component> get(final User user, final CommandCause source, final INucleusServiceCollection serviceCollection) {
        if (serviceCollection.permissionService().hasPermission(source, AFKPermissions.AFK_NOTIFY)) {
            final AFKHandler handler = serviceCollection.getServiceUnchecked(AFKHandler.class);
            final IMessageProviderService messageProviderService = serviceCollection.messageProvider();
            if (user.isOnline()) {
                final UUID uuid = user.getUniqueId();
                final String timeToNow = messageProviderService.getTimeToNow(source.getAudience(), handler.lastActivity(uuid));
                if (handler.canGoAFK(uuid)) {
                    if (handler.isAFK(uuid)) {
                        return Optional.of(
                                messageProviderService.getMessageFor(source.getAudience(), "command.seen.afk",
                                        messageProviderService.getMessageFor(source.getAudience(), "standard.yesno.true"),
                                        timeToNow));
                    } else {
                        return Optional.of(
                                messageProviderService.getMessageFor(source.getAudience(), "command.seen.afk",
                                        messageProviderService.getMessageFor(source.getAudience(), "standard.yesno.false"), timeToNow));
                    }
                } else {
                    return Optional.of(
                            messageProviderService.getMessageFor(source.getAudience(), "command.seen.afk",
                                    messageProviderService.getMessageFor(source.getAudience(), "standard.yesno.false"), timeToNow));
                }
            }
        }

        return Optional.empty();
    }

}
