/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.PlayerInformationService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.User;

import java.util.Collection;
import java.util.Optional;

@ImplementedBy(PlayerInformationService.class)
public interface IPlayerInformationService {

    void registerProvider(Provider provider);

    Collection<Provider> getProviders();

    @FunctionalInterface
    interface Provider {

        Optional<Component> get(User user, CommandCause source, INucleusServiceCollection serviceCollection);

    }
}
