/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.services.target;

import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.UserMessageTarget;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.util.Nameable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public final class PlayerMessageTarget extends AbstractMessageTarget implements UserMessageTarget {

    private final UUID uuid;
    private final IUserPreferenceService userPreferenceService;
    private final IPermissionService permissionService;
    private final IPlayerDisplayNameService playerDisplayNameService;

    public PlayerMessageTarget(final UUID uuid, final IUserPreferenceService service, final IPermissionService permissionService, final
            IPlayerDisplayNameService playerDisplayNameService) {
        this.uuid = uuid;
        this.userPreferenceService = service;
        this.permissionService = permissionService;
        this.playerDisplayNameService = playerDisplayNameService;
    }

    @Override
    public UUID getUserUUID() {
        return this.uuid;
    }

    @Override
    public Optional<Audience> getRepresentedAudience() {
        return Sponge.getServer().getPlayer(this.uuid).map(Function.identity());
    }

    @Override
    public String getName() {
        return Sponge.getServer().getPlayer(this.uuid).map(Nameable::getName).orElse("");
    }

    @Override
    public Component getDisplayName() {
        return this.playerDisplayNameService.getDisplayName(this.uuid);
    }

    @Override
    public void receiveMessageFrom(@Nullable final MessageTarget target, final Component message) {
        this.setReplyTarget(target);
        Sponge.getServer().getPlayer(this.uuid).orElseThrow(() -> new IllegalArgumentException("The player with UUID " + this.uuid.toString() +
                " is not online!")).sendMessage(this.getIdentity(target), message);
    }

    @Override
    public boolean isAvailableForMessages() {
        return Sponge.getServer().getPlayer(this.uuid).isPresent() &&
                this.userPreferenceService.keys().messageReceivingEnabled().flatMap(x -> this.userPreferenceService.get(this.uuid, x)).orElse(true);
    }

    @Override
    public boolean canBypassMessageToggle() {
        return Sponge.getServer().getPlayer(this.uuid).map(x -> this.permissionService.hasPermission(x, MessagePermissions.MSGTOGGLE_BYPASS)).orElse(false);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PlayerMessageTarget that = (PlayerMessageTarget) o;
        return Objects.equals(this.uuid, that.uuid);
    }

    @Override public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public void pushCauseToFrame(final CauseStackManager.StackFrame frame) {
        frame.pushCause(Sponge.getServer().getPlayer(this.uuid).orElseThrow(IllegalStateException::new));
    }
}
