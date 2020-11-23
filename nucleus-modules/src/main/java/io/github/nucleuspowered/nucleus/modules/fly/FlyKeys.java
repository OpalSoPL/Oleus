/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fly;

import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.core.util.GeAnTyRefTypeTokens;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

public final class FlyKeys {

    public static final DataKey<Boolean, IUserDataObject> FLY_TOGGLE = DataKey.of(false, GeAnTyRefTypeTokens.BOOLEAN, IUserDataObject.class, "fly");

}
