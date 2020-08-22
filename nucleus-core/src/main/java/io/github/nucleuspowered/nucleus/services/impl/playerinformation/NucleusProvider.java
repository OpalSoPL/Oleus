/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.playerinformation;

import io.github.nucleuspowered.nucleus.Constants;
import io.github.nucleuspowered.nucleus.annotationprocessor.Store;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerInformationService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import javax.annotation.Nullable;

@Store(Constants.PLAYER_INFO)
public interface NucleusProvider extends IPlayerInformationService.Provider {

    String PUNISHMENT = "punishment";

    String getCategory();

    @Override
    Optional<Text> get(User user, CommandSource source, INucleusServiceCollection serviceCollection);

    @Store(Constants.PLAYER_INFO)
    interface Permission extends NucleusProvider {

        String permission();

        @Nullable
        Text getText(User user, CommandSource source, INucleusServiceCollection serviceCollection);

        @Override
        default Optional<Text> get(final User user, final CommandSource source, final INucleusServiceCollection serviceCollection) {
            if (serviceCollection.permissionService().hasPermission(source, this.permission())) {
                return Optional.ofNullable(this.getText(user, source, serviceCollection));
            }

            return Optional.empty();
        }

    }

}
