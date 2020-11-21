/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.events;

import io.github.nucleuspowered.nucleus.api.module.nickname.event.NucleusChangeNicknameEvent;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;
import java.util.UUID;

public class ChangeNicknameEventPost extends AbstractEvent implements NucleusChangeNicknameEvent.Post {

    private final Cause cause;
    private final UUID target;
    @Nullable private final Component previousNickname;
    @Nullable private final Component newNickname;

    public ChangeNicknameEventPost(final Cause cause, @Nullable final Component previousNickname, @Nullable final Component newNickname,
            final UUID target) {
        this.cause = cause;
        this.previousNickname = previousNickname;
        this.newNickname = newNickname;
        this.target = target;
    }

    @Override public UUID getUser() {
        return this.target;
    }

    @Override
    public Optional<Component> getPreviousNickname() {
        return Optional.ofNullable(this.previousNickname);
    }

    @Override public Optional<Component> getNickname() {
        return Optional.ofNullable(this.newNickname);
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }
}
