/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

public final class MuteKeys {

    public static final TypeToken<MuteData> MUTE_DATA_KEY = TypeToken.of(MuteData.class);

    public static final DataKey<MuteData, IUserDataObject> MUTE_DATA =
            DataKey.of(MuteKeys.MUTE_DATA_KEY, IUserDataObject.class, "muteData");
}
