/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.modules.jail.data.JailData;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IGeneralDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

public final class JailKeys {

    public static final TypeToken<JailData> JAIL_DATA_KEY = TypeToken.of(JailData.class);

    public static final DataKey<JailData, IUserDataObject> JAIL_DATA =
            DataKey.of(JailKeys.JAIL_DATA_KEY, IUserDataObject.class, "jailData");

    public static final DataKey<Boolean, IUserDataObject> JAIL_ON_NEXT_LOGIN =
            DataKey.of(false, TypeTokens.BOOLEAN, IUserDataObject.class, "jailOnNextLogin");

    public static final DataKey.MapKey<String, NamedLocation, IGeneralDataObject> JAILS =
            DataKey.ofMap(TypeTokens.STRING, TypeTokens.NAMEDLOCATION, IGeneralDataObject.class, "jails");
}
