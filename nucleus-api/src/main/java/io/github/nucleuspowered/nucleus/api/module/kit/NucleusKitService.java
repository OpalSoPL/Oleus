/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.kit;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A service for getting and setting kits.
 */
public interface NucleusKitService {

    /**
     * Gets the names of all the kits currently in NucleusPlugin.
     * @return A {@link Set} of {@link String}s.
     */
    Set<String> getKitNames();

    /**
     * Gets the requested kit if it exists.
     *
     * @param name The name of the kit.
     * @return An {@link Optional} that might contain the kit.
     */
    Optional<Kit> getKit(String name);

    /**
     * Returns the items for a kit that the target player would redeem
     * (that is, with token replacements if applicable).
     *
     * @param kit The kit to process
     * @param player The player
     * @return The items.
     */
    Collection<ItemStack> getItemsForPlayer(Kit kit, Player player);

    /**
     * Returns whether the given {@link Kit} has previously been
     * redeemed by the {@link Player}.
     *
     * <p>No assumptions about whether the kit may be redeemed again
     * is made.</p>
     *
     * @param kit The {@link Kit}
     * @param player The {@link User}
     * @return Whether the kit has been redeemed before.
     */
    CompletableFuture<Boolean> hasPreviouslyRedeemed(Kit kit, User player);

    /**
     * Gets whether a {@link Player} can redeem the given {@link Kit}
     * given any cooldown and one-time kit status
     *
     * @param kit The {@link Kit}
     * @param player The {@link User}
     * @return true if so
     */
    CompletableFuture<Boolean> isRedeemable(Kit kit, User player);

    /**
     * If a {@link Player} is unable to redeem the given {@link Kit} as
     * they have previously redeemed it and that the cooldown has not
     * expired, will return the {@link Instant} the {@link User} may
     * redeem the kit again.
     *
     * @param kit The {@link Kit}
     * @param player The {@link Player}
     * @return The {@link Instant} the cooldown expires, if there is a
     *          cooldown in effect.
     */
    CompletableFuture<Optional<Instant>> getCooldownExpiry(Kit kit, User player);

    /**
     * Redeems a kit on a player. Whether the player must get all items is controlled
     * by the Nucleus config.
     *
     * @param kit The kit to redeem
     * @param player The player to redeem the kit against
     * @param performChecks Whether to perform standard permission and cooldown checks
     * @return The {@link KitRedeemResult}
     */
    KitRedeemResult redeemKit(Kit kit, Player player, boolean performChecks);

    /**
     * Redeems a kit on a player.
     *
     * @param kit The kit to redeem
     * @param player The player to redeem the kit against
     * @param performChecks Whether to perform standard permission and cooldown checks
     * @param mustRedeemAll Whether all items must be redeemed to count as a success
     * @return The {@link KitRedeemResult}
     */
    KitRedeemResult redeemKit(Kit kit, Player player, boolean performChecks, boolean mustRedeemAll);

    /**
     * Removes the requested kit.
     *
     * @param kitName The name of the kit to remove.
     * @return <code>true</code> if a kit was removed.
     */
    boolean removeKit(String kitName);

    /**
     * Renames a kit.
     *
     * @param kitName The name of the kit to rename
     * @param newKitName The new name of the kit
     * @throws IllegalArgumentException if either the kit or the target name are unavailable
     */
    void renameKit(String kitName, String newKitName) throws IllegalArgumentException;

    /**
     * Saves a kit.
     *
     * @param kit The kit.
     */
    void saveKit(Kit kit);

    /**
     * Gets a new kit object for use with the Kit service.
     *
     * <p>
     *     Do not make your own kit type, it will not get saved! Use this instead.
     * </p>
     *
     * @param name The name of the kit to create
     * @return The {@link Kit}
     * @throws IllegalArgumentException if the kit name already exists, or is invalid
     */
    Kit createKit(String name) throws IllegalArgumentException;

}
