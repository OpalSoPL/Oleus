/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.api.util.NoExceptionAutoClosable;
import io.github.nucleuspowered.nucleus.services.impl.chatmessageformatter.AbstractNucleusChatChannel;
import io.github.nucleuspowered.nucleus.services.impl.chatmessageformatter.ChatMessageFormatterService;
import net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@ImplementedBy(ChatMessageFormatterService.class)
public interface IChatMessageFormatterService {

    Optional<Channel> getNucleusChannel(UUID uuid);

    void setPlayerNucleusChannel(UUID uuid, @Nullable Channel channel);

    NoExceptionAutoClosable setPlayerNucleusChannelTemporarily(UUID uuid, Channel channel);

    interface Channel {

        default boolean willFormat() {
            return false;
        }

        default void formatMessageEvent(final Player source, final MessageEvent.MessageFormatter formatters) {}

        default void formatMessageEvent(final SystemSubject source, final MessageEvent.MessageFormatter formatters) {}

        default Audience receivers() {
            return Sponge.getServer();
        }

        default boolean ignoreIgnoreList() {
            return false;
        }

        interface External<T extends AbstractNucleusChatChannel<?>> extends Channel {

            T createChannel(Audience delegate);

        }

    }

}
