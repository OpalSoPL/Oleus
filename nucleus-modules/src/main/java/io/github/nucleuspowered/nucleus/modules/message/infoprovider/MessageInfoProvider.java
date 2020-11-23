/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.infoprovider;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.modules.message.MessageKeys;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;

public class MessageInfoProvider implements NucleusProvider {

    @Override
    public String getCategory() {
        return "message";
    }

    @Override
    public Optional<Component> get(final User user, final CommandCause source, final INucleusServiceCollection serviceCollection) {
        if (serviceCollection.permissionService().hasPermission(source, MessagePermissions.BASE_SOCIALSPY)) {
            final MessageHandler handler = serviceCollection.getServiceUnchecked(MessageHandler.class);
            final boolean socialSpy = handler.isSocialSpy(user.getUniqueId());
            final boolean msgToggle = serviceCollection.userPreferenceService()
                    .getUnwrapped(user.getUniqueId(), MessageKeys.MESSAGE_TOGGLE);
            final IMessageProviderService mp = serviceCollection.messageProvider();
            final String yesOrNo = socialSpy ? "standard.yesno.true" : "standard.yesno.false";
            final List<Component> lt = Lists.newArrayList(
                    mp.getMessageFor(source.getAudience(), "seen.socialspy", mp.getMessageFor(source.getAudience(), yesOrNo)));

            /*this.serviceCollection.moduleConfigProvider()
                    .getModuleConfig(MessageConfig.class)*/
            lt.add(
                    mp.getMessageFor(source.getAudience(),
                            "seen.socialspylevel",
                            serviceCollection.permissionService()
                                    .getPositiveIntOptionFromSubject(user, MessagePermissions.SOCIALSPY_LEVEL_KEY).orElse(0))
            );

            final String msgToggleText = msgToggle ? "standard.yesno.true" : "standard.yesno.false";
            lt.add(mp.getMessageFor(source.getAudience(), "seen.msgtoggle", mp.getMessageFor(source.getAudience(), msgToggleText)));

            return Optional.of(Component.join(Component.newline(), lt));
        }

        return Optional.empty();
    }
}
