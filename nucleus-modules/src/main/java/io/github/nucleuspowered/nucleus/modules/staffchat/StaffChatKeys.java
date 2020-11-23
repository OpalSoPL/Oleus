/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.PreferenceKeyImpl;

public final class StaffChatKeys {

    private StaffChatKeys() { }

    public static final NucleusUserPreferenceService.PreferenceKey<Boolean> VIEW_STAFF_CHAT = new PreferenceKeyImpl.BooleanKey(
            NucleusKeysProvider.VIEW_STAFF_CHAT_KEY,
            true,
            StaffChatPermissions.BASE_STAFFCHAT,
            "userpref.viewstaffchat"
    );

}
