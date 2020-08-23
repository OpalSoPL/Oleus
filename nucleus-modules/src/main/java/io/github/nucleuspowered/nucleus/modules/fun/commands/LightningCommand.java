/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fun.commands;

import io.github.nucleuspowered.nucleus.modules.fun.FunPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

@EssentialsEquivalent(value = {"lightning", "strike", "smite", "thor", "shock"}, isExact = false,
        notes = "Selectors can be used, entities can be struck.")
@Command(
        aliases = {"lightning", "strike", "smite", "thor", "shock"},
        basePermission = FunPermissions.BASE_LIGHTNING,
        commandDescriptionKey = "lightning",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = FunPermissions.EXEMPT_COOLDOWN_LIGHTNING),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = FunPermissions.EXEMPT_WARMUP_LIGHTNING),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = FunPermissions.EXEMPT_COST_LIGHTNING)
        },
        associatedPermissions = FunPermissions.OTHERS_LIGHTNING
)
public class LightningCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[]{
                GenericArguments.optional(
                    serviceCollection.commandElementSupplier().createPermissionParameter(
                            NucleusParameters.MANY_LIVING.get(serviceCollection),
                            FunPermissions.OTHERS_LIGHTNING, false))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Collection<Living> playerCollection = context.getAll(NucleusParameters.Keys.SUBJECT, Living.class);

        // No argument, let's not smite the subject.
        if (playerCollection.isEmpty()) {
            final Player pl = context.getIfPlayer();

            // 100 is a good limit here.
            final BlockRay<World> playerBlockRay = BlockRay.from(pl).distanceLimit(100).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();
            final Optional<BlockRayHit<World>> obh = playerBlockRay.end();
            final Location<World> lightningLocation;
            // Smite above, but not on.
            lightningLocation = obh.map(BlockRayHit::getLocation).orElseGet(() -> pl.getLocation().add(0, 3, 0));

            this.spawnLightning(lightningLocation, context, null);
            return context.successResult();
        }

        for (final Living pl : playerCollection) {
            this.spawnLightning(
                    pl.getLocation(),
                    context,
                    pl instanceof Player ? (Player)pl : null);
        }

        return context.successResult();
    }

    private void spawnLightning(
            final Location<World> location,
            final ICommandContext context,
            @Nullable final Player target) throws CommandException {

        final World world = location.getExtent();
        final Entity bolt = world.createEntity(EntityTypes.LIGHTNING, location.getPosition());

        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(context.getCommandSourceRoot());
            world.spawnEntity(bolt);
            if (target != null) {
                context.sendMessage("command.lightning.success.other",
                        context.getServiceCollection().playerDisplayNameService().getDisplayName(target.getUniqueId()));
            }
        }

        if (target != null) {
            throw context.createException("command.lightning.errorplayer", context.getServiceCollection().playerDisplayNameService().getDisplayName(target.getUniqueId()));
        } else {
            throw context.createException("command.lightning.error");
        }

    }
}
