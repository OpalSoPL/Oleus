/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.listeners;

import io.github.nucleuspowered.nucleus.modules.mob.config.BlockSpawnsConfig;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockLivingSpawnListener implements IReloadableService.Reloadable, ListenerBase.Conditional {

    private MobConfig config = new MobConfig();

    @Listener
    public void onConstruct(final ConstructEntityEvent.Pre event, @Getter("getTransform") final Transform<World> worldTransform, @Getter("getTargetType")
    final EntityType type) {
        // No, let's not prevent players from spawning...
        final Class<? extends Entity> entityType = type.getEntityClass();
        if (!checkIsValid(entityType) && !isSpawnable(type, worldTransform.getExtent())) {
            event.setCancelled(true);
        }
    }

    // Most will be caught by the attempt above, but just in case, this catches them.
    @Listener
    public void onSpawn(final SpawnEntityEvent event) {
        event.filterEntities(x -> {
            final Class<? extends Entity> entityType = x.getClass();
            return checkIsValid(entityType) || isSpawnable(x.getType(), x.getWorld());
        });
    }

    // Checks to see if the entity is of a type that should spawn regardless
    private boolean checkIsValid(final Class<? extends Entity> entityType) {
        return !Living.class.isAssignableFrom(entityType) || Player.class.isAssignableFrom(entityType) ||
                ArmorStand.class.isAssignableFrom(entityType);
    }

    private boolean isSpawnable(final EntityType type, final World world) {
        final Optional<BlockSpawnsConfig> bsco = this.config.getBlockSpawnsConfigForWorld(world);
        if (!bsco.isPresent()) {
            return true;
        }

        final String id = type.getId().toLowerCase();
        return !(bsco.get().isBlockVanillaMobs() && id.startsWith("minecraft:") || bsco.get().getIdsToBlock().contains(id));
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.config = serviceCollection.configProvider().getModuleConfig(MobConfig.class);
    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
            if (Sponge.getGame().getState().ordinal() < GameState.SERVER_STARTING.ordinal()) {
                return true;
            }

            final Map<String, BlockSpawnsConfig> conf = serviceCollection.configProvider().getModuleConfig(MobConfig.class).getBlockSpawnsConfig();
            if (conf.entrySet().stream().anyMatch(x -> Sponge.getServer().getWorldProperties(x.getKey()).isPresent())) {
                for (final BlockSpawnsConfig s : conf.values()) {
                    final List<String> idsToBlock = s.getIdsToBlock();
                    if (s.isBlockVanillaMobs() || Sponge.getRegistry().getAllOf(EntityType.class).stream()
                        .anyMatch(x -> idsToBlock.contains(x.getId()))) {
                        return true;
                    }
                }
            }

            return false;
    }
}
