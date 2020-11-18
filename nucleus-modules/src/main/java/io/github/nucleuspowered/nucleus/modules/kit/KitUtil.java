package io.github.nucleuspowered.nucleus.modules.kit;

import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

public final class KitUtil {

    public static ViewableInventory getKitInventoryBuilder() {
        return ViewableInventory.builder().type(ContainerTypes.GENERIC_9x4).fillDummy().completeStructure().build();
    }

    private KitUtil() {
    }

}
