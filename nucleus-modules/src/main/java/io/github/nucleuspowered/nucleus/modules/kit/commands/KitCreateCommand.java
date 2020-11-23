/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.KitUtil;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;

@Command(
        aliases = { "create", "add" },
        basePermission = KitPermissions.BASE_KIT_CREATE,
        commandDescriptionKey = "kit.create",
        parentCommand = KitCommand.class
)
public final class KitCreateCommand implements ICommandExecutor {

    private final Parameter.Value<String> nameParameter = Parameter.string().setKey("name").build();

    private final IMessageProviderService messageProviderService;

    @Inject
    public KitCreateCommand(final IMessageProviderService messageProviderService) {
        this.messageProviderService = messageProviderService;
    }


    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        final IPermissionService permissionService = serviceCollection.permissionService();
        return new Flag[] {
                Flag.builder().setRequirement(cause -> permissionService.hasPermission(cause, KitPermissions.BASE_KIT_EDIT)).aliases("c", "clone").build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.nameParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final KitService service = context.getServiceCollection().getServiceUnchecked(KitService.class);
        final String kitName = context.requireOne(this.nameParameter);

        if (service.getKitNames().stream().anyMatch(kitName::equalsIgnoreCase)) {
            return context.errorResult("command.kit.add.alreadyexists", kitName);
        }

        if (context.is(ServerPlayer.class) && context.testPermission(KitPermissions.BASE_KIT_EDIT)) {
            // if we have a clone request, clone.
            final ServerPlayer player = context.requirePlayer();
            if (context.hasFlag("c")) {
                service.saveKit(service.createKit(kitName).updateKitInventory(player.getInventory()));
                context.sendMessage("command.kit.add.success", kitName);
            } else {
                final InventoryMenu inventory = KitUtil.getKitInventoryBuilder().asMenu();
                inventory.setTitle(context.getMessage("command.kit.create.title", kitName));
                inventory.registerClose((cause, container) -> {
                    if (!service.exists(kitName, true)) {
                        service.saveKit(service.createKit(kitName).updateKitInventory(inventory.getInventory()));
                        this.messageProviderService.sendMessageTo(player, "command.kit.add.success", kitName);
                    } else {
                        this.messageProviderService.sendMessageTo(player, "command.kit.add.alreadyexists", kitName);
                    }
                });
                inventory.open(player).orElseThrow(() -> context.createException("command.kit.create.notcreated"));

            }
        } else {
            try {
                service.saveKit(service.createKit(kitName));
                context.sendMessage("command.kit.addempty.success", kitName);
            } catch (final IllegalArgumentException ex) {
                return context.errorResult("command.kit.create.failed", kitName);
            }
        }

        return context.successResult();
    }

}
