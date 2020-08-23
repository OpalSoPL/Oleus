/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.inventory.commands;

import io.github.nucleuspowered.nucleus.modules.inventory.InventoryPermissions;
import io.github.nucleuspowered.nucleus.modules.inventory.config.InventoryConfig;
import io.github.nucleuspowered.nucleus.modules.inventory.listeners.InvSeeListener;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import java.util.Optional;

@EssentialsEquivalent("invsee")
@Command(
        aliases = {"invsee"},
        basePermission = InventoryPermissions.BASE_INVSEE,
        commandDescriptionKey = "invsee",
        associatedPermissions = {
                InventoryPermissions.INVSEE_EXEMPT_INSPECT,
                InventoryPermissions.INVSEE_EXEMPT_INTERACT,
                InventoryPermissions.INVSEE_MODIFY,
                InventoryPermissions.INVSEE_OFFLINE
        }
)
public class InvSeeCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private boolean self = false;

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.ONE_USER.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User target = context.requireOne(NucleusParameters.Keys.USER, User.class);

        if (!target.isOnline() && !context.testPermission(InventoryPermissions.INVSEE_OFFLINE)) {
            return context.errorResult("command.invsee.nooffline");
        }

        if (!this.self && context.is(target)) {
            return context.errorResult("command.invsee.self");
        }

        if (context.testPermissionFor(target, InventoryPermissions.INVSEE_EXEMPT_INSPECT)) {
            return context.errorResult("command.invsee.targetexempt", target.getName());
        }

        // Just in case, get the subject inventory if they are online.
        try {
            final Player src = context.getIfPlayer();
            final Inventory targetInv = target.isOnline() ? target.getPlayer().get().getInventory() : target.getInventory();
            final Optional<Container> oc = src.openInventory(targetInv);
            if (oc.isPresent()) {
                if (!context.testPermission(InventoryPermissions.INVSEE_MODIFY)
                        || context.testPermissionFor(target, InventoryPermissions.INVSEE_EXEMPT_INTERACT)) {
                    InvSeeListener.addEntry(src.getUniqueId(), oc.get());
                }

                return context.successResult();
            }

            return context.errorResult("command.invsee.failed");
        } catch (final UnsupportedOperationException e) {
            return context.errorResult("command.invsee.offlinenotsupported");
        }
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.self = serviceCollection.moduleDataProvider()
                .getModuleConfig(InventoryConfig.class)
                .isAllowInvseeOnSelf();
    }
}
