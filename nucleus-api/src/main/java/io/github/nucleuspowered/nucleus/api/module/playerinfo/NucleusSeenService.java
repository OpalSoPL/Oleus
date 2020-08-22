/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.playerinfo;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.plugin.PluginContainer;

import java.util.Collection;
import java.util.UUID;

/**
 * This service allows plugins to register handlers that will display
 * information when a player runs the /seen command.
 *
 * <p>
 *     Plugins are expected to only register <strong>one</strong>
 *     {@link SeenInformationProvider}.
 * </p>
 *
 * <p>
 *     Consumers of this API should also note that this might run
 *     <strong>asynchronously</strong>. No methods that would require the use
 *     of a synchronous API should be used here.
 * </p>
 */
public interface NucleusSeenService {

    /**
     * Registers a {@link SeenInformationProvider} with Nucleus.
     *
     * @param plugin The {@link PluginContainer} registering the service.
     * @param seenInformationProvider The {@link SeenInformationProvider}
     * @throws IllegalArgumentException Thrown if the plugin has registered a
     *      {@link SeenInformationProvider} already
     */
    void register(PluginContainer plugin, SeenInformationProvider seenInformationProvider) throws IllegalArgumentException;

    /**
     * A {@link SeenInformationProvider} object can hook into the {@code seen}
     * command and provide extra information on a player.
     *
     * <p>
     *     This must be registered with the {@link NucleusSeenService}
     * </p>
     */
    interface SeenInformationProvider {

        /**
         * Gets whether the requesting {@link CommandCause} has permission to
         * request the provided information for the requested user (presented as
         * a {@link UUID}).
         *
         * @param source The {@link CommandCause} who ran the {@code seen} command.
         * @param user The {@link UUID} of the user that information has been requested about.
         * @return {@code true} if the command should show the user this information.
         */
        boolean hasPermission(@NonNull CommandCause source, @NonNull UUID user);

        /**
         * Gets the information to display to the {@link CommandCause} about the {@link UUID}
         *
         * @param source The {@link CommandCause} who ran the {@code seen}
         *      command.
         * @param user The {@link UUID} of a user that information has been
         *      requested about.
         * @return The {@link Collection} containing the {@link Component} to
         *      display to the user, or an empty iterable. It is recommended,
         *      for obvious reasons, that this is ordered! May return
         *      {@code null} if there is nothing to return.
         */
        @Nullable Collection<Component> getInformation(@NonNull CommandCause source, @NonNull UUID user);
    }
}
