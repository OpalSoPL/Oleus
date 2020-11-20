/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit;

import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.GeAnTyRefTypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

import java.time.Instant;

public final class KitKeys {

    public static final DataKey.MapKey<String, Instant, IUserDataObject> REDEEMED_KITS
            = DataKey.ofMap(GeAnTyRefTypeTokens.STRING, GeAnTyRefTypeTokens.INSTANT, IUserDataObject.class, "usedKits");

}
