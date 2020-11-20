/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.infoprovider;

import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.Optional;

public class MuteInfoProvider implements NucleusProvider {

    @Override public String getCategory() {
        return NucleusProvider.PUNISHMENT;
    }

    @Override public Optional<Text> get(final User user, final CommandSource source, final INucleusServiceCollection serviceCollection) {
        if (serviceCollection.permissionService().hasPermission(source, MutePermissions.BASE_CHECKMUTE)) {
            // If we have a ban service, then check for a ban.
            final MuteHandler jh = serviceCollection.getServiceUnchecked(MuteHandler.class);
            final IMessageProviderService messageProviderService = serviceCollection.messageProvider();
            if (jh.isMuted(user)) {
                final MuteData jd = jh.getPlayerMuteData(user).get();
                // Lightweight checkban.
                final Text.Builder m;
                if (jd.getRemainingTime().isPresent()) {
                    m = messageProviderService.getMessageFor(source, "seen.ismuted.temp",
                            messageProviderService.getTimeString(source.getLocale(), jd.getRemainingTime().get().getSeconds())).toBuilder();
                } else {
                    m = messageProviderService.getMessageFor(source, "seen.ismuted.perm").toBuilder();
                }

                return Optional.of(Text.joinWith(Text.NEW_LINE,
                        m.onClick(TextActions.runCommand("/checkmute " + user.getName()))
                                .onHover(TextActions.showText(
                                        messageProviderService.getMessageFor(source, "standard.clicktoseemore"))).build(),
                        messageProviderService.getMessageFor(source, "standard.reason", jd.getReason())));
            }

            return Optional.of(messageProviderService.getMessageFor(source, "seen.notmuted"));
        }
        return Optional.empty();
    }
}
