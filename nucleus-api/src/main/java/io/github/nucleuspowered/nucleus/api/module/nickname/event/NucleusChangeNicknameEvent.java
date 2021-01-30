/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.nickname.event;

import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Optional;
import java.util.UUID;

/**
 * Fired when a player requests or deletes a nickname.
 *
 * <p>Ensure that you listen for {@link Pre} or {@link Post}, rather than this
 * base event.</p>
 */
@MightOccurAsync
public interface NucleusChangeNicknameEvent extends Event {

    /**
     * The user whose nickname was changed.
     *
     * @return The {@link UUID} of the user
     */
    UUID getUser();

    /**
     * The previous nickname for the {@link #getUser()}
     *
     * @return The previous nickname.
     */
    Optional<Component> getPreviousNickname();

    /**
     * The new nickname, if any, for the {@link #getUser()}
     *
     * @return The nickname, if any is given
     */
    Optional<Component> getNickname();

    @MightOccurAsync
    interface Pre extends NucleusChangeNicknameEvent, Cancellable { }

    @MightOccurAsync
    interface Post extends NucleusChangeNicknameEvent { }

}
