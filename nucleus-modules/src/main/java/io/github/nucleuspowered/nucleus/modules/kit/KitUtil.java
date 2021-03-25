/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit;

import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

public final class KitUtil {

    public static ViewableInventory getKitInventoryBuilder() {
        return ViewableInventory.builder().type(ContainerTypes.GENERIC_9X4).fillDummy().completeStructure().build();
    }

    private KitUtil() {
    }

}
