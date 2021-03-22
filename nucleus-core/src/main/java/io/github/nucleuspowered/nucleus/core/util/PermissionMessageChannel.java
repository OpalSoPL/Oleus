/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.util;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PermissionMessageChannel implements ForwardingAudience {

    private final String permission;
    private final IPermissionService permissionService;

    public PermissionMessageChannel(final IPermissionService service, final String permission) {
        this.permission = permission;
        this.permissionService = service;
    }

    @Override
    public @NonNull Iterable<? extends Audience> audiences() {
        final List<Audience> audiences = new ArrayList<>();
        audiences.add(Sponge.getSystemSubject());
        for (final ServerPlayer pl : Sponge.server().getOnlinePlayers()) {
            if (this.permissionService.hasPermission(pl, this.permission)) {
                audiences.add(pl);
            }
        }

        return audiences;
    }

}
