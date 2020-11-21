/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import io.github.nucleuspowered.nucleus.util.GeAnTyRefTypeTokens;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

public final class PowertoolKeys {

    public static final NucleusUserPreferenceService.PreferenceKey<Boolean> POWERTOOL_ENABLED = new PreferenceKeyImpl.BooleanKey(
                    NucleusKeysProvider.POWERTOOL_ENABLED_KEY,
                    true,
                    PowertoolPermissions.BASE_POWERTOOL,
                    "userpref.powertooltoggle"
            );

    public final static DataKey.MapListKey<String, String, IUserDataObject> POWERTOOLS = DataKey.ofMapList(
                    GeAnTyRefTypeTokens.STRING,
                    GeAnTyRefTypeTokens.STRING,
                    IUserDataObject.class,
                    "powertools"
            );
}
