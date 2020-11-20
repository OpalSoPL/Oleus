/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home;

import io.github.nucleuspowered.nucleus.configurate.datatypes.LocationNode;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.GeAnTyRefTypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

import java.util.Map;

public final class HomeKeys {

    public static DataKey<Map<String, LocationNode>, IUserDataObject> HOMES = DataKey.of(GeAnTyRefTypeTokens.LOCATION_NODES_MAP, IUserDataObject.class, "homes");

}
