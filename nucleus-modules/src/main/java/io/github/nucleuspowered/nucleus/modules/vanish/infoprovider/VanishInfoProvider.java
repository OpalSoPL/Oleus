/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.infoprovider;

import io.github.nucleuspowered.nucleus.modules.vanish.VanishPermissions;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import com.google.inject.Inject;

public class VanishInfoProvider implements NucleusProvider.Permission {

    private final INucleusServiceCollection serviceCollection;

    @Inject
    public VanishInfoProvider(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override public String getCategory() {
        return "vanish";
    }

    @Override public String permission() {
        return VanishPermissions.VANISH_SEE;
    }

    @Nullable
    @Override
    public TextComponent getText(final User user, final CommandSource source, final INucleusServiceCollection serviceCollection) {
        final IMessageProviderService providerService = this.serviceCollection.messageProvider();
        final String isVanished = Boolean.toString(user.get(Keys.VANISH).orElse(false));
        final String yesNo = providerService.getMessageString("standard.yesno." + isVanished.toLowerCase());
        return providerService.getMessageFor(source, "seen.vanish", yesNo);
    }
}
