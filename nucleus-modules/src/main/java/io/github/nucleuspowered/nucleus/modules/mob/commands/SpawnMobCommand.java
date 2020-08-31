/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.commands;

import io.github.nucleuspowered.nucleus.modules.mob.MobPermissions;
import io.github.nucleuspowered.nucleus.modules.mob.config.BlockSpawnsConfig;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@EssentialsEquivalent({"spawnmob", "mob"})
@Command(
        aliases = {"spawnmob", "spawnentity", "mobspawn"},
        basePermission = MobPermissions.BASE_SPAWNMOB,
        commandDescriptionKey = "spawnmob",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MobPermissions.EXEMPT_COOLDOWN_SPAWNMOB),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MobPermissions.EXEMPT_WARMUP_SPAWNMOB),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MobPermissions.EXEMPT_COST_SPAWNMOB)
        },
        associatedPermissions = {
                MobPermissions.OTHERS_SPAWNMOB,
                MobPermissions.SPAWNMOB_MOB
        }
)
public class SpawnMobCommand implements ICommandExecutor, IReloadableService.Reloadable { //extends AbstractCommand.SimpleTargetOtherPlayer implements
    // SimpleReloadable {

    private final String amountKey = "amount";
    private final String mobTypeKey = "mob";

    private MobConfig mobConfig = new MobConfig();

    @Override public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.commandElementSupplier().createOtherUserPermissionElement(true, MobPermissions.OTHERS_SPAWNMOB),
                new ImprovedCatalogTypeArgument(Text.of(this.mobTypeKey), CatalogTypes.ENTITY_TYPE, serviceCollection),
                GenericArguments.optional(new PositiveIntegerArgument(Text.of(this.amountKey), serviceCollection), 1)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player pl = context.getPlayerFromArgs();
        // Get the amount
        final int amount = context.requireOne(this.amountKey, Integer.class);
        final EntityType et = context.requireOne(this.mobTypeKey, EntityType.class);

        if (!Living.class.isAssignableFrom(et.getEntityClass())) {
            return context.errorResult("command.spawnmob.livingonly", et.getTranslation().get());
        }

        final String id = et.getId().toLowerCase();
        if (this.mobConfig.isPerMobPermission() && !context.isConsoleAndBypass() && !context.testPermission(MobPermissions.getSpawnMobPermissionFor(et))) {
            return context.errorResult("command.spawnmob.mobnoperm", et.getTranslation().get());
        }

        final Optional<BlockSpawnsConfig> config = this.mobConfig.getBlockSpawnsConfigForWorld(pl.getWorld());
        if (config.isPresent() && (config.get().isBlockVanillaMobs() && id.startsWith("minecraft:") || config.get().getIdsToBlock().contains(id))) {
            return context.errorResult("command.spawnmob.blockedinconfig", et.getTranslation().get());
        }

        final Location<World> loc = pl.getLocation();
        final World w = loc.getExtent();

        // Count the number of entities spawned.
        int i = 0;

        Entity entityone = null;
        do {
            final Entity e = w.createEntity(et, loc.getPosition());
            if (!w.spawnEntity(e)) {
                return context.errorResult("command.spawnmob.fail", Text.of(e));
            }

            if (entityone == null) {
                entityone = e;
            }

            i++;
        } while (i < Math.min(amount, this.mobConfig.getMaxMobsToSpawn()));

        if (amount > this.mobConfig.getMaxMobsToSpawn()) {
            context.sendMessage("command.spawnmob.limit", String.valueOf(this.mobConfig.getMaxMobsToSpawn()));
        }

        if (i == 0) {
            return context.errorResult("command.spawnmob.fail", et.getTranslation().get());
        }

        if (i == 1) {
            context.sendMessage("command.spawnmob.success.singular", Text.of(i), Text.of(entityone));
        } else {
            context.sendMessage("command.spawnmob.success.plural", Text.of(i), Text.of(entityone));
        }

        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.mobConfig = serviceCollection.configProvider().getModuleConfig(MobConfig.class);
    }


}
