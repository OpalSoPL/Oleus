/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.playername.PlayerDisplayNameService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Nameable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ImplementedBy(PlayerDisplayNameService.class)
public interface IPlayerDisplayNameService {

    void provideDisplayNameResolver(DisplayNameResolver resolver);

    void provideDisplayNameQuery(DisplayNameQuery resolver);

    Optional<User> getUser(TextComponent displayName);

    Optional<User> getUser(String displayName);

    /**
     * Gets the {@link UUID} of the players that have a name or display name
     * that starts with the given parameter.
     *
     * @param displayName The display name
     * @return The {@link UUID}
     */
    Map<UUID, List<String>> startsWith(String displayName);

    TextComponent getDisplayName(UUID playerUUID);

    default TextComponent getDisplayName(final Player player) {
        return this.getDisplayName(player.getUniqueId());
    }

    default TextComponent getDisplayName(final User user) {
        return this.getDisplayName(user.getUniqueId());
    }

    TextComponent getDisplayName(Audience source);

    TextComponent getName(Audience user);

    TextComponent getName(Nameable user);

    TextComponent addCommandToName(Nameable p);

    TextComponent addCommandToDisplayName(Nameable p);

    @FunctionalInterface
    interface DisplayNameResolver {

        Optional<TextComponent> resolve(UUID userUUID);

    }

    interface DisplayNameQuery {

        Optional<User> resolve(String name);

        Map<UUID, String> startsWith(String name);

    }

}
