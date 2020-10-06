/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core;

import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.GeAnTyRefTypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;
import io.github.nucleuspowered.storage.dataobjects.keyed.IKeyedDataObject;
import io.leangen.geantyref.TypeToken;

import java.time.Instant;

public final class CoreKeys {

    @SuppressWarnings("unchecked")
    private static final Class<IKeyedDataObject<?>> keyedDataObjectClass =
            (Class<IKeyedDataObject<?>>) new TypeToken<IKeyedDataObject<?>>() {}.getType();

    public static DataKey<Integer, IKeyedDataObject<?>> VERSION = DataKey.of(GeAnTyRefTypeTokens.INTEGER, keyedDataObjectClass, "version");

    public static DataKey<String, IUserDataObject> LAST_KNOWN_NAME = DataKey.of(GeAnTyRefTypeTokens.STRING, IUserDataObject.class, "lastKnownName");

    public static DataKey<Instant, IUserDataObject> LAST_LOGIN = DataKey.of(GeAnTyRefTypeTokens.INSTANT, IUserDataObject.class, "lastLogin");

    public static DataKey<Instant, IUserDataObject> LAST_LOGOUT = DataKey.of(GeAnTyRefTypeTokens.INSTANT, IUserDataObject.class, "lastLogout");

    public static DataKey<String, IUserDataObject> IP_ADDRESS = DataKey.of(GeAnTyRefTypeTokens.STRING, IUserDataObject.class, "lastIP");

    @Deprecated
    public static DataKey<Instant, IUserDataObject> FIRST_JOIN = DataKey.of(GeAnTyRefTypeTokens.INSTANT, IUserDataObject.class, "firstJoin");

}
