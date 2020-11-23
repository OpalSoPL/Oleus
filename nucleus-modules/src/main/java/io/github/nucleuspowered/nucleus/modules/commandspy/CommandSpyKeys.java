/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandspy;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.core.services.impl.userprefs.PreferenceKeyImpl;

public final class CommandSpyKeys {

    private CommandSpyKeys() {}

    public static final NucleusUserPreferenceService.PreferenceKey<Boolean> COMMAND_SPY = new PreferenceKeyImpl.BooleanKey(
            NucleusKeysProvider.COMMAND_SPY_KEY,
            true,
            CommandSpyPermissions.BASE_COMMANDSPY,
            "userpref.commandspy"
    );

}
