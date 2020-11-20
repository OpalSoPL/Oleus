/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ignore;

import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.GeAnTyRefTypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

import java.util.List;
import java.util.UUID;

public final class IgnoreKeys {

    public static DataKey<List<UUID>, IUserDataObject> IGNORED = DataKey.of(GeAnTyRefTypeTokens.UUID_LIST, IUserDataObject.class, "ignoreList");

}
