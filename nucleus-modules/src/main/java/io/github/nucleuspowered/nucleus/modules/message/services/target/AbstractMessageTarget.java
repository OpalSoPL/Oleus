package io.github.nucleuspowered.nucleus.modules.message.services.target;

import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.UserMessageTarget;
import net.kyori.adventure.identity.Identity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.CauseStackManager;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class AbstractMessageTarget implements MessageTarget {

    AbstractMessageTarget() {}

    @Nullable
    private WeakReference<MessageTarget> target;

    public final void setReplyTarget(@Nullable final MessageTarget messageTarget) {
        if (messageTarget == null) {
            this.target = null;
        } else {
            this.target = new WeakReference<>(messageTarget);
        }
    }

    final Identity getIdentity(final MessageTarget messageTarget) {
        if (messageTarget instanceof UserMessageTarget) {
            return Identity.identity(((UserMessageTarget) messageTarget).getUserUUID());
        } else {
            return Identity.nil();
        }
    }

    @Override
    public final Optional<? extends MessageTarget> replyTarget() {
        if (this.target == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.target.get());
    }

    public abstract void pushCauseToFrame(final CauseStackManager.StackFrame frame);

}
