/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.modules.admin.AdminPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Nameable;

import java.util.Collection;

@Command(aliases = "kill", basePermission = AdminPermissions.BASE_KILL, commandDescriptionKey = "kill",
    modifiers = {
            @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = AdminPermissions.EXEMPT_WARMUP_KILL),
            @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = AdminPermissions.EXEMPT_COOLDOWN_KILL),
            @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = AdminPermissions.EXEMPT_COST_KILL)
    })
@EssentialsEquivalent(value = { "kill", "remove", "butcher", "killall", "mobkill" },
        isExact = false, notes = "Nucleus supports killing entities using the Minecraft selectors.")
public class KillCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MANY_ENTITY
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Collection<Entity> entities = context.requireOne(NucleusParameters.MANY_ENTITY);

        int entityKillCount = 0;
        int playerKillCount = 0;
        for (final Entity x : entities) {
            final DataTransactionResult dtr = x.offer(Keys.HEALTH, 0d);
            if (!dtr.isSuccessful() && !(x instanceof Living)) {
                x.remove();
            }
            entityKillCount++;

            if (x instanceof Player) {
                final Player pl = (Player) x;
                playerKillCount++;
                context.sendMessage("command.kill.killed", pl.getName());
                final Object root = context.getCause().root();
                final String name;
                if (root instanceof Nameable) {
                    name = ((Nameable) root).getName();
                } else if (root instanceof SystemSubject) {
                    name = context.getMessageStringFor(pl, "standard.console");
                } else {
                    name = context.getMessageStringFor(pl, "standard.unknown");
                }
                context.sendMessageTo(pl, "command.kill.killedby", name);
            }
        }

        if (entityKillCount > playerKillCount) {
            context.sendMessage("command.kill.overall", entityKillCount, playerKillCount);
        }

        return context.successResult();
    }
}
