/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.jail;

import io.github.nucleuspowered.nucleus.api.module.jail.data.Jail;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jailing;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A service that handles subject jailing.
 */
public interface NucleusJailService {

    /**
     * The permission context key that indicates a player is jailed.
     *
     * <p>The value of this context will always be true if set.</p>
     */
    String JAILED_CONTEXT = "nucleus_jailed";

    /**
     * The permission context key that indicates which jail a player is in.
     *
     * <p>The value of this context will be the name of the jail.</p>
     */
    String JAIL_CONTEXT = "nucleus_jail";

    /**
     * Sets a jail location in the world.
     *
     * @param name The name of the jail to use.
     * @param location The {@link Location} in a world for the jail.
     * @param rotation The rotation of the subject once in jail.
     * @return the jail, if it could be created
     */
    Optional<Jail> setJail(String name, ServerLocation location, Vector3d rotation);

    /**
     * Gets the name of the jails on the server. All jails returned in this map exist.
     *
     * @return A {@link Map} of names to {@link NamedLocation}.
     */
    Map<String, Jail> getJails();

    /**
     * Gets the location of a jail, if it exists.
     *
     * @param name The name of the jail to get. Case insensitive.
     * @return An {@link Optional} that potentially contains the {@link NamedLocation} if the jail exists.
     */
    Optional<Jail> getJail(String name);

    /**
     * Removes a jail location from the list.
     *
     * @param name The name of the jail to remove.
     * @return <code>true</code> if successful.
     */
    boolean removeJail(String name);

    /**
     * Returns whether a subject is jailed.
     *
     * @param user The {@link UUID} of the user to check.
     * @return <code>true</code> if the subject is jailed.
     */
    boolean isPlayerJailed(UUID user);

    /**
     * Returns information about why a subject is jailed, if they are indeed jailed.
     *
     * @param uuid The {@link UUID} of the user to check
     * @return An {@link Optional} that will contain {@link Jailing} information if the subject is jailed.
     */
    Optional<Jailing> getPlayerJailData(UUID uuid);

    /**
     * Jails a subject if they are not currently jailed. The current {@link Cause}
     * will be used to determine who the jailer is.
     *
     * @param victim The user to jail.
     * @param jail The jail to send the user to.
     * @param reason The reason for jailing.
     * @param duration The {@link Duration} the user should be jailed for,
     *          if temporary.
     * @return <code>true</code> if the subject was jailed successfully.
     */
    boolean jailPlayer(UUID victim, Jail jail, String reason, @Nullable Duration duration);

    /**
     * Unjails a subject if they are currently jailed.
     *
     * @param user The {@link UUID} of the user to unjail.
     * @return <code>true</code> if the subject was unjailed successfully.
     */
    boolean unjailPlayer(UUID user);
}
