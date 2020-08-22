/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.staffchat;

import net.kyori.adventure.audience.Audience;

/**
 * Indicates that the chat event is a staff chat message.
 *
 * <p>Note that this is only guaranteed to appear after
 * {@link org.spongepowered.api.event.Order#LATE}</p>
 */
public interface NucleusStaffChatChannel extends Audience { }
