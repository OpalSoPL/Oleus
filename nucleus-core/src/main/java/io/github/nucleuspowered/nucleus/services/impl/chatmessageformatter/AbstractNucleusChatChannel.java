/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.chatmessageformatter;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public abstract class AbstractNucleusChatChannel<T extends Collection<MessageReceiver>> implements MessageChannel {

    private final MessageChannel messageChannel;
    final T messageReceiverList;

    public AbstractNucleusChatChannel(final MessageChannel messageChannel, final T receivers) {
        this.messageChannel = messageChannel;
        this.messageReceiverList = receivers;
    }

    @Override
    public void send(@Nullable final Object sender, final Text original, final ChatType type) {
        this.messageChannel.send(sender, original, type);
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        final Collection<MessageReceiver> delegated = this.messageChannel.getMembers();
        return this.messageReceiverList.stream().filter(delegated::contains).collect(Collectors.toList());
    }

    MessageChannel getDelegated() {
        return this.messageChannel;
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
