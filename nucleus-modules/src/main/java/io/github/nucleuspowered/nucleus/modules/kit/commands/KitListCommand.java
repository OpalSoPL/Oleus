/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitKeys;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IEconomyServiceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Command(
        aliases = { "list", "ls", "#kits" },
        basePermission = KitPermissions.BASE_KIT_LIST,
        commandDescriptionKey = "kit.list",
        parentCommand = KitCommand.class,
        associatedPermissions = KitPermissions.KIT_SHOWHIDDEN
)
public class KitListCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final KitService service = context.getServiceCollection().getServiceUnchecked(KitService.class);
        final Set<String> kits = service.getKitNames();
        if (kits.isEmpty()) {
            return context.errorResult("command.kit.list.empty");
        }

        final PaginationList.Builder paginationService = Util.getPaginationBuilder(context.getAudience());
        final ArrayList<Component> kitText = new ArrayList<>();

        final Map<String, Instant> redeemed =
                context.is(Player.class) ? context.getServiceCollection()
                        .storageManager()
                        .getUserService()
                        .getOrNewOnThread(context.getIfPlayer().getUniqueId())
                        .getNullable(KitKeys.REDEEMED_KITS) : null;

        final boolean showHidden = context.testPermission(KitPermissions.KIT_SHOWHIDDEN);
        service.getKitNames(showHidden).stream()
                .filter(kit -> context.testPermission(KitPermissions.getKitPermission(kit.toLowerCase())))
                .forEach(kit -> kitText.add(this.createKit(context, redeemed, kit, service.getKit(kit).get())));

        final PaginationList.Builder paginationBuilder = paginationService.contents(kitText)
                .title(context.getMessage("command.kit.list.kits"))
                .padding(Component.text("-", NamedTextColor.GREEN));
        paginationBuilder.sendTo(context.getAudience());

        return context.successResult();
    }

    private Component createKit(final ICommandContext context, @Nullable final Map<String, Instant> user, final String kitName, final Kit kitObj) {
        final TextComponent.Builder tb = Component.text().content(kitName);

        if (user != null) {
            final Instant lastRedeem = user.get(kitName.toLowerCase());
            if (lastRedeem != null) {
                // If one time used...
                if (kitObj.isOneTime() && !context.testPermission(KitPermissions.KIT_EXEMPT_ONETIME)) {
                    return tb.color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(context.getMessage("command.kit.list.onetime", kitName)))
                            .style(Style.style(TextDecoration.STRIKETHROUGH)).build();
                }

                // If an intervalOld is used...
                final Duration interval = kitObj.getCooldown().orElse(Duration.ZERO);
                if (!interval.isZero() && !context.testPermission(KitPermissions.KIT_EXEMPT_COOLDOWN)) {

                    // Get the next time the kit can be used.
                    final Instant next = lastRedeem.plus(interval);
                    if (next.isAfter(Instant.now())) {
                        // Get the time to next usage.
                        final String time = context.getTimeString(Duration.between(Instant.now(), next));
                        return tb.color(NamedTextColor.RED)
                                .hoverEvent(HoverEvent.showText(context.getMessage("command.kit.list.interval", kitName, time)))
                                .style(Style.style(TextDecoration.STRIKETHROUGH)).build();
                    }
                }
            }
        }

        // Can use.
        final TextComponent.Builder builder = tb.color(NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/kit \"" + kitName + "\""))
                .hoverEvent(HoverEvent.showText(context.getMessage("command.kit.list.text", kitName)))
                .style(Style.style(TextDecoration.ITALIC));
        final IEconomyServiceProvider economyServiceProvider = context.getServiceCollection().economyServiceProvider();
        if (kitObj.getCost() > 0 && economyServiceProvider.serviceExists() && !context.testPermission(KitPermissions.KIT_EXEMPT_COST)) {
            builder.append(context.getMessage("command.kit.list.cost", economyServiceProvider.getCurrencySymbol(kitObj.getCost())));
        }

        return builder.build();
    }
}
