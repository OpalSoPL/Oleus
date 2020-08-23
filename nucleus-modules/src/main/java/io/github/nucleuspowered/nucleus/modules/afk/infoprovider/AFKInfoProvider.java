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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class AFKInfoProvider implements NucleusProvider {

    @Override
    public String getCategory() {
        return "afk";
    }

    @Override
    public Optional<Text> get(final User user, final CommandSource source, final INucleusServiceCollection serviceCollection) {
        if (serviceCollection.permissionService().hasPermission(source, AFKPermissions.AFK_NOTIFY)) {
            final AFKHandler handler = serviceCollection.getServiceUnchecked(AFKHandler.class);
            final IMessageProviderService messageProviderService = serviceCollection.messageProvider();
            if (user.isOnline()) {
                final Player player = user.getPlayer().get();
                final String timeToNow = messageProviderService.getTimeToNow(source.getLocale(), handler.lastActivity(player));
                if (handler.canGoAFK(player)) {
                    if (handler.isAFK(player)) {
                        return Optional.of(
                                messageProviderService.getMessageFor(source.getLocale(), "command.seen.afk",
                                        messageProviderService.getMessageFor(source.getLocale(), "standard.yesno.true"),
                                        timeToNow));
                    } else {
                        return Optional.of(
                                messageProviderService.getMessageFor(source.getLocale(), "command.seen.afk",
                                        messageProviderService.getMessageFor(source.getLocale(), "standard.yesno.false"), timeToNow));
                    }
                } else {
                    return Optional.of(
                            messageProviderService.getMessageFor(source.getLocale(), "command.seen.afk",
                                    messageProviderService.getMessageFor(source.getLocale(), "standard.yesno.false"), timeToNow));
                }
            }
        }

        return Optional.empty();
    }
}
