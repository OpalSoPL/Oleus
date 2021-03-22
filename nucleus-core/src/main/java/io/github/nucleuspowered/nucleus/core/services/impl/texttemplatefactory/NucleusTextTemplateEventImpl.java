/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.api.text.event.NucleusTextTemplateEvent;
import io.github.nucleuspowered.nucleus.core.services.interfaces.INucleusTextTemplateFactory;
import net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

class NucleusTextTemplateEventImpl extends AbstractEvent implements NucleusTextTemplateEvent {

    private boolean cancelled = false;
    private final NucleusTextTemplate originalMessage;
    private NucleusTextTemplate message;
    private final INucleusTextTemplateFactory textTemplateFactory;
    private final Audience originalMembers;
    private Audience members;
    private final Cause cause;

    NucleusTextTemplateEventImpl(
            final NucleusTextTemplate originalMessage,
            final Audience originalMembers,
            final INucleusTextTemplateFactory textTemplateFactory,
            final Cause cause) {
        this.originalMessage = originalMessage;
        this.message = originalMessage;
        this.originalMembers = originalMembers;
        this.members = this.originalMembers;
        this.cause = cause;
        this.textTemplateFactory = textTemplateFactory;
    }

    @Override
    public NucleusTextTemplate getMessage() {
        return this.message;
    }

    @Override
    public NucleusTextTemplate getOriginalMessage() {
        return this.originalMessage;
    }

    @Override
    public void setMessage(final NucleusTextTemplate message) {
        this.message = message;
    }

    @Override public void setMessage(final String message) {
        try {
            this.textTemplateFactory.createFromAmpersandString(message);
        } catch (final Throwable throwable) {
            throw new IllegalArgumentException("Could not create text", throwable);
        }
    }

    @Override
    public Audience getOriginalAudience() {
        return this.originalMembers;
    }

    @Override
    public Audience getAudience() {
        return this.members;
    }

    @Override
    public void setAudience(final Audience recipients) {
        this.members = recipients;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    @NonNull
    public Cause cause() {
        return this.cause;
    }

    static class Broadcast extends NucleusTextTemplateEventImpl implements NucleusTextTemplateEvent.Broadcast {

        Broadcast(final NucleusTextTemplate originalMessage,
                final Audience originalMembers,
                final INucleusTextTemplateFactory factory,
                final Cause cause) {
            super(originalMessage, originalMembers, factory, cause);
        }
    }
}
