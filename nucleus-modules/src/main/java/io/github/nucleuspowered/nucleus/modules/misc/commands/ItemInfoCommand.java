/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.misc.MiscPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EssentialsEquivalent(value = {"itemdb", "itemno", "durability", "dura"}, isExact = false, notes = "Nucleus tries to provide much more info!")
@Command(
        aliases = { "iteminfo", "itemdb" },
        basePermission = MiscPermissions.BASE_ITEMINFO,
        commandDescriptionKey = "iteminfo",
        associatedPermissions = MiscPermissions.ITEMINFO_EXTENDED
)
public class ItemInfoCommand implements ICommandExecutor {

    private final String key = "key";

    private final Parameter.Value<ItemStackSnapshot> itemTypeParameter = Parameter.itemStackSnapshot().optional().setKey("item").build();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        final IPermissionService permissionService = serviceCollection.permissionService();
        return new Flag[] {
                Flag.builder().alias("e").alias("extended").setRequirement(commandCause -> permissionService.hasPermission(commandCause,
                        MiscPermissions.ITEMINFO_EXTENDED)).build()
        };
    }


    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.itemTypeParameter
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<ItemStackSnapshot> catalogTypeOptional = context.getOne(this.itemTypeParameter);
        final ItemStackSnapshot is;
        if (catalogTypeOptional.isPresent()) {
            is = catalogTypeOptional.get();
        } else if (context.is(ServerPlayer.class) && !context.requirePlayer().getItemInHand(HandTypes.MAIN_HAND).isEmpty()) {
            is = context.getIfPlayer().getItemInHand(HandTypes.MAIN_HAND).createSnapshot();
        } else {
            return context.errorResult("command.iteminfo.none");
        }

        final ItemType it = is.getType();
        final List<Component> lt = new ArrayList<>();
        lt.add(context.getMessage("command.iteminfo.id", it.getKey(), it.asComponent()));

        it.getBlock().ifPresent(block -> lt.add(context.getMessage("command.iteminfo.extendedid", block.getKey().asString())));

        if (context.hasFlag("e")) {
            for (final Key<? extends Value<?>> key : is.getKeys()) {
                final Optional<?> value = is.get((Key) key); // this is the only way I could get this to work
                value.ifPresent(o -> lt.add(context.getMessage("command.iteminfo.key", key.getKey(), String.valueOf(o))));
            }
        }

        Util.getPaginationBuilder(context.getAudience())
                .contents(lt)
                .padding(Component.text("-", NamedTextColor.GREEN))
                .title(context.getMessage("command.iteminfo.list.header"))
                .sendTo(context.getAudience());
        return context.successResult();
    }
}
