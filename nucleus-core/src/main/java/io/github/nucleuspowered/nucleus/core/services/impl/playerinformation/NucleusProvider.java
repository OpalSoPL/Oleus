/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.playerinformation;

import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerInformationService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface NucleusProvider extends IPlayerInformationService.Provider {

    String PUNISHMENT = "punishment";

    String getCategory();

    interface Permission extends NucleusProvider {

        String permission();

        @Nullable
        Component getText(User user, CommandCause source, INucleusServiceCollection serviceCollection);

        @Override
        default Optional<Component> get(final User user, final CommandCause source, final INucleusServiceCollection serviceCollection) {
            if (serviceCollection.permissionService().hasPermission(source, this.permission())) {
                return Optional.ofNullable(this.getText(user, source, serviceCollection));
            }

            return Optional.empty();
        }

    }

}
