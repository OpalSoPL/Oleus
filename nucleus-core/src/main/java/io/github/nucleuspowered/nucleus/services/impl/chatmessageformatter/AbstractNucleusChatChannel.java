/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.chatmessageformatter;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;

import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public abstract class AbstractNucleusChatChannel<T extends Collection<Audience>> implements ForwardingAudience {

    private final ForwardingAudience messageChannel;
    final T messageReceiverList;

    public AbstractNucleusChatChannel(final ForwardingAudience messageChannel, final T receivers) {
        this.messageChannel = messageChannel;
        this.messageReceiverList = receivers;
    }

    public abstract static class Immutable<I extends Immutable<I, M>, M extends Mutable> extends AbstractNucleusChatChannel<ImmutableList<MessageReceiver>> {

        private final Function<Immutable<I, M>, M> mutableFactory;

        public Immutable(final MessageChannel messageChannel,
                final Collection<MessageReceiver> messageReceivers,
                final Function<Immutable<I, M>, M> mutableFactory) {
            super(messageChannel, ImmutableList.copyOf(messageReceivers));
            this.mutableFactory = mutableFactory;
        }

        @Override
        public M asMutable() {
            return this.mutableFactory.apply(this);
        }
    }

    public abstract static class Mutable<M extends Mutable<M>>
            extends AbstractNucleusChatChannel<Set<MessageReceiver>>
            implements MutableMessageChannel {

        public Mutable(final Immutable<?, M> immutable) {
            super(immutable.getDelegated(), new HashSet<>(immutable.getMembers()));
        }

        public Mutable(final MessageChannel messageChannel, final Collection<MessageReceiver> messageReceivers) {
            super(messageChannel, new HashSet<>(messageReceivers));
        }

        @Override
        public boolean addMember(final MessageReceiver member) {
            return this.messageReceiverList.add(member);
        }

        @Override
        public boolean removeMember(final MessageReceiver member) {
            return this.messageReceiverList.remove(member);
        }

        @Override
        public void clearMembers() {
            this.messageReceiverList.clear();
        }

        @Override
        public Mutable<M> asMutable() {
            return this;
        }
    }

}
