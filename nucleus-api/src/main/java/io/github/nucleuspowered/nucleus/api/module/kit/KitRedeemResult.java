/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.kit;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.api.module.kit.event.NucleusKitEvent;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

/**
 * The result for a successful redemption.
 */
public interface KitRedeemResult {

    /**
     * Whether this redemption was a success.
     *
     * @return true if so
     */
    default boolean isSuccess() {
        return this.getStatus() == Status.SUCCESS || this.getStatus() == Status.PARTIAL_SUCCESS;
    }

    /**
     * Gets the status of this result.
     *
     * @return The status.
     */
    Status getStatus();

    /**
     * Specifies when the <em>next</em> redemption opportunity is.
     *
     * <ul>
     *     <li>If {@link #isSuccess()} is true, this returns the next
     *     time the {@link Kit} can be redeemed by the player.</li>
     *     <li>Else, if there is still a cooldown attached to this
     *     kit and player, the next time this kit may be redeemed.
     *     </li>
     * </ul>
     *
     * @return The {@link Instant} redemption may next happen, if a cooldown is
     *      currently in effect.
     */
    Optional<Instant> getCooldownExpiry();

    /**
     * Returns the {@link Duration} from {@link #getCooldownExpiry()}
     * to {@link Instant#now()}
     *
     * @return The {@link Duration}, if any.
     */
    default Optional<Duration> getCooldownDuration() {
        return this.getCooldownExpiry().map(x -> {
            Instant now = Instant.now();
            if (x.isAfter(now)) {
                return Duration.between(now, x);
            }
            return null;
        });
    }

    /**
     * If the redemption was cancelled by an event, gets the associated message.
     *
     * @return The associated message, if any.
     */
    Optional<Component> getMessage();

    /**
     * The items that didn't make it into the inventory if the kit was otherwise
     * successfully redeemed.
     *
     * @return The rejected items.
     */
    Collection<ItemStackSnapshot> rejectedItems();

    enum Status {

        /**
         * The redemption was a success, and everything was redeemed
         */
        SUCCESS,

        /**
         * The redemption was a success, but some items were rejected
         */
        PARTIAL_SUCCESS,

        /**
         * The one time kit has already been redeemed.
         */
        ALREADY_REDEEMED_ONE_TIME,

        /**
         * The cooldown has not expired.
         */
        COOLDOWN_NOT_EXPIRED,

        /**
         * There is no space for the items in the kit.
         */
        NO_SPACE,

        /**
         * The {@link NucleusKitEvent.Redeem.Pre} event was cancelled.
         */
        PRE_EVENT_CANCELLED,

        /**
         * An unknown error occurred.
         */
        UNKNOWN

    }
}
