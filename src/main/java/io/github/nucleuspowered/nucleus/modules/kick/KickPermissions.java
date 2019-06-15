/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kick;

import io.github.nucleuspowered.nucleus.annotationprocessor.RegisterPermissions;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionMetadata;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;

@RegisterPermissions
public class KickPermissions {
    private KickPermissions() {
        throw new AssertionError("Nope");
    }

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "kick" }, level = SuggestedLevel.MOD)
    public static final String BASE_KICK = "kick.base";

    @PermissionMetadata(descriptionKey = "permission.kick.exempt.target", level = SuggestedLevel.MOD)
    public static final String KICK_EXEMPT_TARGET = "kick.exempt.target";

    @PermissionMetadata(descriptionKey = "permission.kick.notify", level = SuggestedLevel.MOD)
    public static final String KICK_NOTIFY = "kick.notify";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "kickall" }, level = SuggestedLevel.MOD)
    public static final String BASE_KICKALL = "kickall.base";

    @PermissionMetadata(descriptionKey = "permission.kickall.whitelist", level = SuggestedLevel.ADMIN)
    public static final String KICKALL_WHITELIST = "kickall.whitelist";

}