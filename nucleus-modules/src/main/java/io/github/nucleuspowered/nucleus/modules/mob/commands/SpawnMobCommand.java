/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.commands;

import io.github.nucleuspowered.nucleus.modules.mob.MobPermissions;
import io.github.nucleuspowered.nucleus.modules.mob.config.BlockSpawnsConfig;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

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
public class SpawnMobCommand implements ICommandExecutor, IReloadableService.Reloadable {

    @SuppressWarnings("unchecked")
    private final Parameter.Value<EntityType<?>> entityTypeParameter = Parameter.builder(new TypeToken<EntityType<?>>() {})
            .parser((ValueParameter<EntityType<?>>) (Object) VariableValueParameters.catalogedElementParameterBuilder(EntityType.class).build())
            .setKey("entity type")
            .build();

    private final Parameter.Value<Integer> amount = Parameter.builder(Integer.class)
            .setKey("amount")
            .parser(VariableValueParameters.integerRange().setMin(1).build())
            .optional()
            .build();

    private MobConfig mobConfig = new MobConfig();

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherPlayerPermissionElement(MobPermissions.OTHERS_SPAWNMOB),
                this.entityTypeParameter,
                this.amount
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer pl = context.getPlayerFromArgs();
        // Get the amount
        final int amount = context.getOne(this.amount).orElse(1);
        final EntityType<?> et = context.requireOne(this.entityTypeParameter);

        if (this.mobConfig.isPerMobPermission() && !context.isConsoleAndBypass() && !context.testPermission(MobPermissions.getSpawnMobPermissionFor(et))) {
            return context.errorResult("command.spawnmob.mobnoperm", et.asComponent());
        }

        final String id = et.getKey().asString().toLowerCase();
        final Optional<BlockSpawnsConfig> config = this.mobConfig.getBlockSpawnsConfigForWorld(pl.getWorld());
        if (config.isPresent() && (config.get().isBlockVanillaMobs() && id.startsWith("minecraft:") || config.get().getIdsToBlock().contains(id))) {
            return context.errorResult("command.spawnmob.blockedinconfig", et.asComponent());
        }


        final ServerLocation loc = pl.getServerLocation();
        final ServerWorld w = loc.getWorld();

        // Count the number of entities spawned.
        int i = 0;

        Entity entityone = null;
        do {
            final Entity e = w.createEntity(et, loc.getPosition());
            if (!(e instanceof Living)) {
                e.remove();
                return context.errorResult("command.spawnmob.livingonly", et.asComponent());
            }

            if (!w.spawnEntity(e)) {
                return context.errorResult("command.spawnmob.fail", et.asComponent());
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
            return context.errorResult("command.spawnmob.fail", et.asComponent());
        }

        if (i == 1) {
            context.sendMessage("command.spawnmob.success.singular", Component.text(i), et.asComponent().hoverEvent(entityone.asHoverEvent()));
        } else {
            context.sendMessage("command.spawnmob.success.plural", Component.text(i), et.asComponent().hoverEvent(entityone));
        }

        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.mobConfig = serviceCollection.configProvider().getModuleConfig(MobConfig.class);
    }


}
