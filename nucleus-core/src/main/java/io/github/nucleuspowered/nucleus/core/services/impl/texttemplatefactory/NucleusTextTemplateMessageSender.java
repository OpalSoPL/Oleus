/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.api.text.event.NucleusTextTemplateEvent;
import io.github.nucleuspowered.nucleus.core.services.interfaces.INucleusTextTemplateFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.CauseStackManager;

public class NucleusTextTemplateMessageSender {

    private final INucleusTextTemplateFactory textTemplateFactory;
    private final NucleusTextTemplate textTemplate;
    private final Object sender;

    public NucleusTextTemplateMessageSender(
            final INucleusTextTemplateFactory textTemplateFactory,
            final NucleusTextTemplate textTemplate,
            final Object sender) {
        this.textTemplateFactory = textTemplateFactory;
        this.textTemplate = textTemplate;
        this.sender = sender;
    }

    public boolean send() {
        return this.send(Sponge.server(), true);
    }

    public boolean send(final Audience audience) {
        return this.send(audience, false);
    }

    public boolean send(final Audience originalMembers, final boolean isBroadcast) {
        final NucleusTextTemplateEvent event;
        try (final CauseStackManager.StackFrame frame = Sponge.server().getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(this.sender);
            final Cause cause = Sponge.server().getCauseStackManager().getCurrentCause();
            if (isBroadcast) {
                event = new NucleusTextTemplateEventImpl.Broadcast(
                        this.textTemplate,
                        originalMembers,
                        this.textTemplateFactory,
                        cause
                );
            } else {
                event = new NucleusTextTemplateEventImpl(
                        this.textTemplate,
                        originalMembers,
                        this.textTemplateFactory,
                        cause
                );
            }

            if (Sponge.getEventManager().post(event)) {
                return false;
            }

            final NucleusTextTemplate template = event.getMessage();
            if (!template.containsTokens() || event.getAudience() instanceof ForwardingAudience) {
                final Component text = this.textTemplate.getForObjectWithSenderToken(event.getAudience(), this.sender);
                event.getAudience().sendMessage(text);
            } else {
                final ForwardingAudience forwardingAudience = (ForwardingAudience) event.getAudience();
                forwardingAudience.audiences().forEach(x -> {
                    x.sendMessage(this.textTemplate.getForObjectWithSenderToken(event.getAudience(), this.sender));
                });
            }
            return true;
        }
    }
}
