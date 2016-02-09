package uk.co.drnaylor.minecraft.quickstart.commands.misc;

import com.google.common.collect.Sets;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import uk.co.drnaylor.minecraft.quickstart.Util;
import uk.co.drnaylor.minecraft.quickstart.api.PluginModule;
import uk.co.drnaylor.minecraft.quickstart.argumentparsers.RequireOneOfPermission;
import uk.co.drnaylor.minecraft.quickstart.internal.CommandBase;
import uk.co.drnaylor.minecraft.quickstart.internal.PermissionUtil;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.Modules;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.Permissions;

import java.util.Optional;
import java.util.Set;

@Permissions
@Modules(PluginModule.MISC)
public class FeedCommand extends CommandBase {
    private static final String player = "player";

    @Override
    public CommandSpec createSpec() {
        Set<String> ss = Sets.newHashSet(PermissionUtil.PERMISSIONS_PREFIX + "feed.others", PermissionUtil.PERMISSIONS_ADMIN);

        return CommandSpec.builder().executor(this).arguments(
                new RequireOneOfPermission(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.player(Text.of(player)))), ss)
        ).build();
    }

    @Override
    public String[] getAliases() {
        return new String[] { "feed" };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Optional<Player> opl = args.<Player>getOne(player);
        Player pl;
        if (opl.isPresent()) {
            pl = opl.get();
        } else {
            if (src instanceof Player) {
                pl = (Player)src;
            } else {
                src.sendMessage(Text.of(TextColors.RED, Util.messageBundle.getString("command.playeronly")));
                return CommandResult.empty();
            }
        }

        // TODO: If max food level appears, use that instead.
        if (pl.offer(Keys.FOOD_LEVEL, 20).isSuccessful()) {
            pl.sendMessages(Text.of(TextColors.GREEN, Util.messageBundle.getString("command.feed.success")));
            if (!pl.equals(src)) {
                src.sendMessages(Text.of(TextColors.GREEN, Util.getMessageWithFormat("command.feed.success.other", pl.getName())));
            }

            return CommandResult.success();
        } else {
            src.sendMessages(Text.of(TextColors.RED, Util.messageBundle.getString("command.feed.error")));
            return CommandResult.empty();
        }
    }
}