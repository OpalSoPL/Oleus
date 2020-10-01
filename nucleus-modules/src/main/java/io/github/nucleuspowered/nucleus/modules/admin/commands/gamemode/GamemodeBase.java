/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands.gamemode;

import io.github.nucleuspowered.nucleus.modules.admin.AdminPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

abstract class GamemodeBase implements ICommandExecutor {

    private static final Map<String, String> MODE_MAP = new HashMap<String, String>() {{
        this.put(GameModes.SURVIVAL.get().getKey().getValue(), AdminPermissions.GAMEMODE_MODES_SURVIVAL);
        this.put(GameModes.CREATIVE.get().getKey().getValue(), AdminPermissions.GAMEMODE_MODES_CREATIVE);
        this.put(GameModes.ADVENTURE.get().getKey().getValue(), AdminPermissions.GAMEMODE_MODES_ADVENTURE);
        this.put(GameModes.SPECTATOR.get().getKey().getValue(), AdminPermissions.GAMEMODE_MODES_SPECTATOR);
    }};

    ICommandResult baseCommand(final ICommandContext context, final Player user, final Supplier<GameMode> sgm) throws CommandException {
        return this.baseCommand(context, user, sgm.get());
    }

    ICommandResult baseCommand(final ICommandContext context, final Player user, final GameMode gm) throws CommandException {
        if (!context.testPermission(MODE_MAP.computeIfAbsent(
                gm.getKey().asString(), key -> {
                    final String[] keySplit = key.split(":", 2);
                    final String r = keySplit[keySplit.length - 1].toLowerCase();
                    final String perm = AdminPermissions.GAMEMODE_MODES_ROOT + "." + r;
                    MODE_MAP.put(key, perm);
                    return perm;
                }
        ))) {
            return context.errorResult("command.gamemode.permission", gm.getKey().getValue());
        }

        final DataTransactionResult dtr = user.offer(Keys.GAME_MODE, gm);
        if (dtr.isSuccessful()) {
            if (!context.is(user)) {
                context.sendMessage("command.gamemode.set.other", user.getName(), gm.getKey().getValue());
            }

            context.sendMessageTo(user, "command.gamemode.set.base", gm.getKey().getValue());
            return context.successResult();
        }

        return context.errorResult("command.gamemode.error", user.getName());
    }
}
