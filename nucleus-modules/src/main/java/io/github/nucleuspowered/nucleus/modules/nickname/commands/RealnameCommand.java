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
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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

    private final String playerKey = "name";

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            GenericArguments.string(Text.of(playerKey))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String argname = context.requireOne(this.playerKey, String.class);

        final NicknameService service = context.getServiceCollection().getServiceUnchecked(NicknameService.class);
        final Map<Player, Text> names = service.getFromSubstring(argname.toLowerCase());
        names.forEach((player, text) -> {

        });

        if (names.isEmpty()) {
            context.sendMessage("command.realname.nonames", argname);
        } else {
            final List<Text> realNames = new ArrayList<>();
            for (final Map.Entry<Player, Text> entry : names.entrySet()) {
                realNames.add(Text.of(entry.getKey().getName(), TextColors.GRAY, " -> ", TextColors.WHITE, entry.getValue()));
            }

            final PaginationList.Builder plb = Util.getPaginationBuilder(context.getCommandSourceRoot())
                    .contents(realNames)
                    .padding(Text.of(TextColors.GREEN, "-"))
                    .title(context.getMessage("command.realname.title", argname));
            plb.sendTo(context.getCommandSourceRoot());
        }

        return context.successResult();
    }

}
