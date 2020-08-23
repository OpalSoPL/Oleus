/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class ImprovedGameModeArgument extends CommandElement {

    private final static Map<String, GameMode> GAMEMODE_MAP = Maps.newHashMap();

    static {
        GAMEMODE_MAP.put("survival", GameModes.SURVIVAL);
        GAMEMODE_MAP.put("s", GameModes.SURVIVAL);
        GAMEMODE_MAP.put("su", GameModes.SURVIVAL);
        GAMEMODE_MAP.put("0", GameModes.SURVIVAL);
        GAMEMODE_MAP.put("creative", GameModes.CREATIVE);
        GAMEMODE_MAP.put("c", GameModes.CREATIVE);
        GAMEMODE_MAP.put("1", GameModes.CREATIVE);
        GAMEMODE_MAP.put("adventure", GameModes.ADVENTURE);
        GAMEMODE_MAP.put("a", GameModes.ADVENTURE);
        GAMEMODE_MAP.put("2", GameModes.ADVENTURE);
        GAMEMODE_MAP.put("spectator", GameModes.SPECTATOR);
        GAMEMODE_MAP.put("sp", GameModes.SPECTATOR);
        GAMEMODE_MAP.put("3", GameModes.SPECTATOR);
    }

    private final IMessageProviderService messageProviderService;

    public ImprovedGameModeArgument(@Nullable final TextComponent key, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String arg = args.next();
        final GameMode mode = GAMEMODE_MAP.get(arg.toLowerCase());

        if (mode == null) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.gamemode.error", arg));
        }

        return mode;
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        try {
            final String arg = args.peek();
            return GAMEMODE_MAP.keySet().stream().filter(x -> x.startsWith(arg.toLowerCase())).collect(Collectors.toList());
        } catch (final ArgumentParseException e) {
            return Lists.newArrayList(GAMEMODE_MAP.keySet());
        }
    }

    @Override
    public TextComponent getUsage(final CommandSource src) {
        return Text.of("game mode");
    }
}
