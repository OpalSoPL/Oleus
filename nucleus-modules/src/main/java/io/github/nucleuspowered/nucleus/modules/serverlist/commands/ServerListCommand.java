/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.serverlist.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.serverlist.ServerListPermissions;
import io.github.nucleuspowered.nucleus.modules.serverlist.config.ServerListConfig;
import io.github.nucleuspowered.nucleus.modules.serverlist.services.ServerListService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.managed.Flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Command(aliases = {"serverlist", "sl"}, basePermission = ServerListPermissions.BASE_SERVERLIST, commandDescriptionKey = "serverlist")
public class ServerListCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private ServerListConfig slc = new ServerListConfig();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("m", "messages"),
                Flag.of("w", "whitelist")
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Display current information
        if (context.hasFlag("m")) {
            this.onMessage(context, this.slc.getMessages(), "command.serverlist.head.messages");
            return context.successResult();
        } else if (context.hasFlag("w")) {
            this.onMessage(context, this.slc.getWhitelist(), "command.serverlist.head.whitelist");
            return  context.successResult();
        }

        if (this.slc.isModifyServerList()) {
            context.sendMessage("command.serverlist.modify.true");
            if (!this.slc.getMessages().isEmpty()) {
                context.sendMessageText(context.getMessage("command.serverlist.messages.click")
                        .clickEvent(ClickEvent.runCommand("/nucleus:serverlist -m")));
            }

            if (!this.slc.getWhitelist().isEmpty()) {
                context.sendMessageText(context.getMessage("command.serverlist.whitelistmessages.click")
                        .clickEvent(ClickEvent.runCommand("/nucleus:serverlist -w")));
            }
        } else if (this.slc.getModifyServerList() == ServerListConfig.ServerListSelection.WHITELIST) {
            context.sendMessage("command.serverlist.modify.whitelist");

            if (!this.slc.getWhitelist().isEmpty()) {
                context.sendMessageText(context.getMessage("command.serverlist.whitelistmessages.click")
                                .clickEvent(ClickEvent.runCommand("/nucleus:serverlist -w")));
            }
        } else {
            context.sendMessage("command.serverlist.modify.false");
        }

        final ServerListService ss = context.getServiceCollection().getServiceUnchecked(ServerListService.class);
        ss.getMessage().ifPresent(
                t -> {
                    context.sendMessageText(Component.empty());
                    context.sendMessage("command.serverlist.tempheader");
                    context.sendMessageText(t);
                    context.sendMessage("command.serverlist.message.expiry",
                            context.getTimeToNowString(ss.getExpiry().get()));
                }
            );

        if (this.slc.isHidePlayerCount()) {
            context.sendMessage("command.serverlist.hideplayers");
        } else if (this.slc.isHideVanishedPlayers()) {
            context.sendMessage("command.serverlist.hidevanished");
        }

        return context.successResult();
    }

    private void onMessage(final ICommandContext context, final List<String> messages, final String key) throws CommandException {
        if (messages.isEmpty()) {
            throw context.createException("command.serverlist.nomessages");
        }

        final Audience source = context.getAudience();
        final List<Component> m = new ArrayList<>();
        messages.stream()
                .map(x -> context.getServiceCollection().textTemplateFactory().createFromAmpersandStringIgnoringExceptions(x).orElse(null))
                .filter(Objects::nonNull)
                .map(x -> x.getForObject(source)).forEach(x -> {
            if (!m.isEmpty()) {
                m.add(Component.empty());
            }

            m.add(x);
        });

        if (m.isEmpty()) {
            throw context.createException("command.serverlist.nomessages");
        }

        Util.getPaginationBuilder(source).contents(m).title(context.getMessage(key)).sendTo(source);
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.slc = serviceCollection.configProvider().getModuleConfig(ServerListConfig.class);
    }
}
