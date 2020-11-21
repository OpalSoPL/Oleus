/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.nickname.NicknamePermissions;
import io.github.nucleuspowered.nucleus.modules.nickname.services.NicknameService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EssentialsEquivalent("realname")
@Command(
        aliases = {"realname"},
        basePermission = NicknamePermissions.BASE_REALNAME,
        commandDescriptionKey = "realname"
)
public class RealnameCommand implements ICommandExecutor {

    private final Parameter.Value<String> parameter = Parameter.string().setKey("name").build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            this.parameter
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String argname = context.requireOne(this.parameter);

        final NicknameService service = context.getServiceCollection().getServiceUnchecked(NicknameService.class);
        final Map<Player, Component> names = service.getFromSubstring(argname.toLowerCase());
        names.forEach((player, text) -> {

        });

        if (names.isEmpty()) {
            context.sendMessage("command.realname.nonames", argname);
        } else {
            final List<Component> realNames = new ArrayList<>();
            for (final Map.Entry<Player, Component> entry : names.entrySet()) {
                realNames.add(LinearComponents.linear(
                        Component.text(entry.getKey().getName()),
                        Component.text(" -> ", NamedTextColor.GRAY),
                        entry.getValue().color(NamedTextColor.WHITE)));
            }

            final PaginationList.Builder plb = Util.getPaginationBuilder(context.getAudience())
                    .contents(realNames)
                    .padding(Component.text(" - ", NamedTextColor.GREEN))
                    .title(context.getMessage("command.realname.title", argname));
            plb.sendTo(context.getAudience());
        }

        return context.successResult();
    }

}
