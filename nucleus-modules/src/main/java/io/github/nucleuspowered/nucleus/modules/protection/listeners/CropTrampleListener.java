/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.protection.listeners;

import io.github.nucleuspowered.nucleus.modules.protection.config.ProtectionConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class CropTrampleListener implements IReloadableService.Reloadable, ListenerBase.Conditional {

    private boolean cropentity = false;
    private boolean cropplayer = false;

    // Not sure this should be correct. Keep an eye.
    @Listener
    public void onBlockChange(final ChangeBlockEvent.All breakEvent, @Root final Entity entity) {
        // If player or entity and the corresponding option is added
        final boolean isPlayer = entity instanceof ServerPlayer;
        if (this.cropplayer && isPlayer || this.cropentity && !isPlayer) {
            // Go from Farmland to Dirt.
            breakEvent.getTransactions().stream()
                    .filter(Transaction::isValid)
                    .filter(x -> x.getOriginal().getState().getType().equals(BlockTypes.FARMLAND.get()))
                    .filter(x -> x.getFinal().getState().getType().equals(BlockTypes.DIRT.get()))
                    .forEach(x -> x.setValid(false));
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final ProtectionConfig protectionConfig = serviceCollection.configProvider().getModuleConfig(ProtectionConfig.class);
        this.cropentity = protectionConfig.isDisableMobCropTrample();
        this.cropplayer = protectionConfig.isDisablePlayerCropTrample();
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(ProtectionConfig.class).isDisableAnyCropTrample();
    }
}
