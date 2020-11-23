/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolKeys;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolPermissions;
import io.github.nucleuspowered.nucleus.modules.powertool.services.PowertoolService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(
        aliases = {"list", "ls"},
        basePermission = PowertoolPermissions.BASE_POWERTOOL,
        commandDescriptionKey = "powertool.list",
        parentCommand = PowertoolCommand.class
)
public class ListPowertoolCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer serverPlayer = context.requirePlayer();
        final UUID uuid = serverPlayer.getUniqueId();
        final boolean toggle = context.getServiceCollection().userPreferenceService()
                .getUnwrapped(uuid, PowertoolKeys.POWERTOOL_ENABLED);

        final PowertoolService service = context.getServiceCollection().getServiceUnchecked(PowertoolService.class);
        final Map<String, List<String>> powertools = service.getPowertools(uuid);

        if (powertools.isEmpty()) {
            return context.errorResult("command.powertool.list.none");
        }

        // Generate powertools.
        final List<Component> mesl = powertools.entrySet().stream().sorted((a, b) -> a.getKey()
                .compareToIgnoreCase(b.getKey()))
                .map(k -> this.from(service, serverPlayer, context, k.getKey(), k.getValue())).collect(Collectors.toList());

        // Paginate the tools.
        Util.getPaginationBuilder(context.getAudience()).title(
                context.getMessage("command.powertool.list.header", toggle ? "&aenabled" : "&cdisabled"))
                .padding(Component.text("-", NamedTextColor.YELLOW))
                .contents(mesl)
                .sendTo(context.getAudience());

        return context.successResult();
    }

    private Component from(
            final PowertoolService service,
            final ServerPlayer src,
            final ICommandContext context,
            final String powertool,
            final List<String> commands) {

        final Optional<ItemType> oit = Sponge.getRegistry().getCatalogRegistry().get(ItemType.class, ResourceKey.resolve(powertool));
        final UUID uuid = src.getUniqueId();

        // Create the click actions.
        final ClickEvent viewAction = SpongeComponents.executeCallback(pl -> Util.getPaginationBuilder(src)
                .title(context.getMessage("command.powertool.ind.header", powertool))
                .padding(Component.text("-", NamedTextColor.GREEN))
                .contents(commands.stream().map(x -> Component.text(x, NamedTextColor.YELLOW)).collect(Collectors.toList())).sendTo(src));

        final ClickEvent deleteAction = SpongeComponents.executeCallback(pl -> {
            service.clearPowertool(uuid, powertool);
            context.sendMessage("command.powertool.removed", powertool);
        });

        final TextColor tc = oit.map(itemType -> NamedTextColor.YELLOW).orElse(NamedTextColor.GRAY);

        // id - [View] - [Delete]
        return LinearComponents.linear(
                Component.text(powertool, tc),
                Component.text(" - "),
                context.getMessage("standard.view").color(NamedTextColor.YELLOW).clickEvent(viewAction),
                Component.text(" - "),
                context.getMessage("standard.delete").color(NamedTextColor.DARK_RED).clickEvent(deleteAction));
    }
}
