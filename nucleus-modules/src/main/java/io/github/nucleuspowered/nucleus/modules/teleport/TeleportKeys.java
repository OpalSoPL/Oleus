package io.github.nucleuspowered.nucleus.modules.teleport;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;

public final class TeleportKeys {

    public static final NucleusUserPreferenceService.PreferenceKey<Boolean> TELEPORT_TOGGLE = new PreferenceKeyImpl.BooleanKey(
            NucleusKeysProvider.TELEPORT_TARGETABLE_KEY,
            true,
            TeleportPermissions.BASE_TPTOGGLE,
            "userpref.teleporttarget"
    );

    private TeleportKeys() {}



}
