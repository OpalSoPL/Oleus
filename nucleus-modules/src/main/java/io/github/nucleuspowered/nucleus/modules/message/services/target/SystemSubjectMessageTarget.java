package io.github.nucleuspowered.nucleus.modules.message.services.target;

import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.SystemMessageTarget;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.CauseStackManager;

import java.util.Optional;

public final class SystemSubjectMessageTarget extends AbstractMessageTarget implements SystemMessageTarget {

    private final Component serverDisplayName;

    public SystemSubjectMessageTarget(final Component serverDisplayName) {
        this.serverDisplayName = serverDisplayName;
    }

    @Override
    public Optional<Audience> getRepresentedAudience() {
        return Optional.of(Sponge.getSystemSubject());
    }

    @Override
    public Component getDisplayName() {
        return this.serverDisplayName;
    }

    @Override
    public void receiveMessageFrom(final MessageTarget messageTarget, final Component message) {
        this.setReplyTarget(messageTarget);
        Sponge.getSystemSubject().sendMessage(this.getIdentity(messageTarget), message);
    }

    @Override
    public boolean isAvailableForMessages() {
        return true;
    }

    @Override
    public void pushCauseToFrame(final CauseStackManager.StackFrame frame) {
        frame.pushCause(Sponge.getSystemSubject());
    }

    @Override
    public boolean canBypassMessageToggle() {
        return true;
    }
}
