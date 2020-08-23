/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.modules.admin.parameter.AdminParameters;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.parameter.CommonParameters;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.math.vector.Vector3d;
import com.google.common.collect.Sets;
import io.github.nucleuspowered.nucleus.modules.admin.AdminPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Command(aliases = "kill", basePermission = AdminPermissions.BASE_KILL, commandDescriptionKey = "kill",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = AdminPermissions.EXEMPT_WARMUP_KILLENTITY),
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = AdminPermissions.EXEMPT_COOLDOWN_KILLENTITY),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = AdminPermissions.EXEMPT_COST_KILLENTITY)
        })
public class KillEntityCommand implements ICommandExecutor {

    private final Parameter.Value<Integer> radius = Parameter.builder(Integer.class).setKey("radius").parser(
            VariableValueParameters.integerRange().setMin(0).setMax(Integer.MAX_VALUE).build()
    ).build();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder().alias("r").setParameter(this.radius).build(),
                Flag.builder().alias("w").setParameter(CommonParameters.ONLINE_WORLD_PROPERTIES_ONLY).build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                AdminParameters.ENTITY_PARAMETER
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final CommandCause src = context.getCause();
        if (!(src.getLocation().isPresent()) && context.hasAny(radius)) {
            // We can't do that.
            return context.errorResult("command.killentity.commandsourceradius");
        }

        if (context.hasAny(radius) && context.hasAny(world)) {
            // Can't do that, either.
            return context.errorResult("command.killentity.radiusworld");
        }

        final Set<Entity> currentEntities;
        if (context.hasAny(radius)) {
            final Locatable l = ((Locatable) src);
            final Vector3d locationTest = l.getLocation().getPosition();
            final int r = context.requireOne(radius, int.class);
            currentEntities = Sets.newHashSet(l.getWorld().getEntities(entity -> entity.getTransform().getPosition().distance(locationTest) <= r));
        } else {
            final WorldProperties worldProperties;
            if (context.hasAny(world)) {
                worldProperties = context.requireOne(world, WorldProperties.class);
            } else {
                worldProperties = ((Locatable) src).getWorld().getProperties();
            }
            currentEntities = Sets.newHashSet(Sponge.getServer().getWorld(worldProperties.getUniqueId()).get().getEntities());
        }

        final Predicate<Entity> entityPredicate = context.getAll(type, TypeTokens.PREDICATE_ENTITY).stream().reduce(Predicate::or)
                .orElseThrow(() -> context.createException("command.killentity.noselection"));
        final Set<Entity> toKill = currentEntities.stream().filter(entityPredicate).collect(Collectors.toSet());
        if (toKill.isEmpty()) {
            return context.errorResult("command.killentity.nothing");
        }

        final int killCount = toKill.size();
        toKill.forEach(x -> {
            x.offer(Keys.HEALTH, 0d);
            x.remove();
        });

        context.sendMessage("command.killentity.success", String.valueOf(killCount));
        return context.successResult();
    }


}
