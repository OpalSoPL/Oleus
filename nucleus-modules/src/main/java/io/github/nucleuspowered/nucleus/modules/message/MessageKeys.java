/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.PreferenceKeyImpl;

public final class MessageKeys {

    public static final NucleusUserPreferenceService.PreferenceKey<Boolean> MESSAGE_TOGGLE = new PreferenceKeyImpl.BooleanKey(
            NucleusKeysProvider.MESSAGE_TOGGLE_KEY,
            true,
            MessagePermissions.MSGTOGGLE_BYPASS,
            "userpref.messagetoggle"
    );

    public static final NucleusUserPreferenceService.PreferenceKey<Boolean> SOCIAL_SPY = new PreferenceKeyImpl.BooleanKey(
            NucleusKeysProvider.SOCIAL_SPY_KEY,
            true,
            ((serviceCollection, user) -> serviceCollection.permissionService().hasPermission(user, MessagePermissions.BASE_SOCIALSPY)
                    && !serviceCollection.permissionService().hasPermission(user, MessagePermissions.SOCIALSPY_FORCE)),
            "userpref.socialspy"
    );

}
