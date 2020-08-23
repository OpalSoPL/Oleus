/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.events;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.api.module.afk.event.NucleusAFKEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AFKEvents extends AbstractEvent implements TargetPlayerEvent, NucleusAFKEvent {

    private final Player target;
    private final Cause cause;
    private final MessageChannel original;
    private MessageChannel channel;
    @Nullable private final TextComponent originalMessage;
    @Nullable private TextComponent message;

    AFKEvents(final Player target, @Nullable final TextComponent message, @Nullable final MessageChannel original) {
        this(target, message, original, Cause.of(EventContext.builder().add(EventContextKeys.OWNER, target).build(), target));
    }

    AFKEvents(final Player target, @Nullable final TextComponent message, @Nullable final MessageChannel original, final Cause cause) {
        this.target = target;
        this.cause = cause;
        this.originalMessage = message;
        this.message = message;
        this.original = original;
        this.channel = original;
    }

    @Override
    public Player getTargetEntity() {
        return this.target;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Optional<Text> getOriginalMessage() {
        return Optional.ofNullable(this.originalMessage);
    }

    @Override
    public Optional<Text> getMessage() {
        return Optional.ofNullable(this.message);
    }

    @Override
    public void setMessage(@Nullable final TextComponent message) {
        this.message = message;
    }

    @Override
    public MessageChannel getOriginalChannel() {
        return this.original;
    }

    @Override
    public MessageChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(final MessageChannel channel) {
        this.channel = Preconditions.checkNotNull(channel);
    }

    public static class From extends AFKEvents implements NucleusAFKEvent.ReturningFromAFK {

        public From(final Player target, @Nullable final TextComponent message, @Nullable final MessageChannel original, final Cause cause) {
            super(target, message, original, cause);
        }
    }

    public static class To extends AFKEvents implements NucleusAFKEvent.GoingAFK {

        public To(final Player target, @Nullable final TextComponent message, @Nullable final MessageChannel original) {
            super(target, message, original);
        }

        public To(final Player target, @Nullable final TextComponent message, @Nullable final MessageChannel original, final Cause cause) {
            super(target, message, original, cause);
        }
    }

    public static class Kick extends AFKEvents implements NucleusAFKEvent.Kick {

        private boolean cancelled = false;

        public Kick(final Player target, final TextComponent message, final MessageChannel original) {
            super(target, message, original);
        }

        public Kick(final Player target, final TextComponent message, final MessageChannel original, final Cause cause) {
            super(target, message, original, cause);
        }

        @Override public boolean isCancelled() {
            return this.cancelled;
        }

        @Override public void setCancelled(final boolean cancel) {
            this.cancelled = cancel;
        }
    }

    public static class Notify implements NucleusAFKEvent.NotifyCommand {

        private final Player target;
        private final Cause cause;
        @Nullable private final TextComponent originalMessage;
        @Nullable private TextComponent message;

        public Notify(final Player target, @Nullable final TextComponent message, final Cause cause) {
            this.target = target;
            this.originalMessage = message;
            this.message = message;
            this.cause = cause;
        }

        @Override public Cause getCause() {
            return this.cause;
        }

        @Override public Optional<Text> getOriginalMessage() {
            return Optional.ofNullable(this.originalMessage);
        }

        @Override public Optional<Text> getMessage() {
            return Optional.ofNullable(this.message);
        }

        @Override public Player getTargetEntity() {
            return this.target;
        }

        @Override public void setMessage(@Nullable final TextComponent message) {
            this.message = message;
        }
    }
}
