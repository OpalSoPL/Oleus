/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.text.event;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplateFactory;
import io.github.nucleuspowered.nucleus.api.util.MightOccurAsync;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

/**
 * Event when messages are sent using {@link NucleusTextTemplate}s.
 */
@MightOccurAsync
public interface NucleusTextTemplateEvent extends Event, Cancellable {

    /**
     * Get the {@link NucleusTextTemplate} that will be parsed and sent to
     * players.
     *
     * @return The message
     */
    NucleusTextTemplate getMessage();

    /**
     * Get the original {@link NucleusTextTemplate}.
     *
     * @return The message
     */
    NucleusTextTemplate getOriginalMessage();

    /**
     * Sets the message to send to the users {@link #getAudience()}
     *
     * @param message The message to send.
     */
    void setMessage(NucleusTextTemplate message);

    /**
     * Attempts to set the NucleusTextTemplate message using a string.
     *
     * <p>See {@link NucleusTextTemplateFactory#createFromAmpersandString(String)}
     * for creating the tokens. Also see {@link #setMessage(NucleusTextTemplate)}.
     * </p>
     *
     * @param message The message to send.
     */
    void setMessage(String message);

    /**
     * Get the original {@link Audience} to send the message to.
     *
     * @return The original {@link Audience} of the message.
     */
    Audience getOriginalAudience();

    /**
     * Get the {@link Audience} to send the message to.
     *
     * @return The recipients.
     */
    Audience getAudience();

    /**
     * Set the {@link Audience} to send the message to.
     *
     * @param audience The audience.
     */
    void setAudience(Audience audience);

    /**
     * Whether the message contains tokens that may be replaced.
     *
     * @return true if so.
     */
    default boolean containsTokens() {
        return this.getMessage().containsTokens();
    }

    /**
     * Gets the message that would be sent to the specified
     * {@link Audience}.
     *
     * <p>It is recommended that you use an {@link Audience} that is either a
     * {@link ServerPlayer} or {@link SystemSubject} to see the effect on a
     * specific member. Using a {@link ForwardingAudience} will result in a
     * generic message.</p>
     *
     * @param source The source
     * @return The message for the specific source
     */
    default Component getMessageFor(final Audience source) {
        return Component.text().append(this.getMessage().getForObject(source)).build();
    }

    /**
     * Raised when the text being sent originated as a broadcast.
     */
    interface Broadcast extends NucleusTextTemplateEvent {

        /**
         * Resets the the broadcast to the original recipients - all players
         */
        default void sendToAll() {
            this.setAudience(Sponge.getServer());
        }
    }
}
