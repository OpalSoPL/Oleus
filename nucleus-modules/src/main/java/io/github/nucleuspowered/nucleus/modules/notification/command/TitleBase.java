/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.command;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.modules.notification.config.NotificationConfig;
import io.github.nucleuspowered.nucleus.modules.notification.config.TitleConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class TitleBase implements ICommandExecutor, IReloadableService.Reloadable {

    private final String multiplePerm;
    private final String type;
    private final TextComponent fadeIn = Text.of("fade in");
    private final TextComponent fadeOut = Text.of("fade out");
    private final TextComponent time = Text.of("time");
    private TitleConfig titleConfig = new TitleConfig();

    protected TitleBase(final String multiplePerm, final String type) {
        this.multiplePerm = multiplePerm;
        this.type = type;
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                GenericArguments.flags()
                        .valueFlag(NucleusParameters.MANY_PLAYER.get(serviceCollection), "p")
                        .valueFlag(GenericArguments.doubleNum(this.fadeIn), "i")
                        .valueFlag(GenericArguments.doubleNum(this.fadeOut), "o")
                        .valueFlag(GenericArguments.doubleNum(this.time), "t")
                        .buildWith(NucleusParameters.MESSAGE)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // If we don't have a player, check we can send to all.
        final Collection<Player> targets;
        if (!context.hasAny(NucleusParameters.Keys.PLAYER)) {
            targets = Sponge.getServer().getOnlinePlayers();
        } else {
            targets = context.getAll(NucleusParameters.Keys.PLAYER, Player.class);
        }

        if (targets.isEmpty()) {
            return context.errorResult("command.title.noonline");
        }

        if (targets.size() > 1 && !context.testPermission(this.multiplePerm)) {
            return context.errorResult("command.title.multi.noperms");
        }

        final CommandSource sender = context.getCommandSourceRoot();
        final String message = context.requireOne(NucleusParameters.Keys.MESSAGE, String.class);
        final NucleusTextTemplate textTemplate =
                context.getServiceCollection().textTemplateFactory().createFromAmpersandString(message);
        final Title.Builder builder = Title.builder()
                .fadeIn(toTicks(context, this.fadeIn, this.titleConfig::getFadeIn))
                .fadeOut(toTicks(context, this.fadeOut, this.titleConfig::getFadeOut))
                .stay(toTicks(context, this.time, this.titleConfig::getTime));
        if (targets.size() > 1) {
            for (final Player pl : targets) {
                pl.sendTitle(applyToBuilder(builder, textTemplate.getForCommandSource(pl, sender)).build());
            }
            context.sendMessage("command.title.player.success.multi", this.type, targets.size());
        } else {
            final Player pl = targets.iterator().next();
            final TextComponent t = textTemplate.getForCommandSource(pl, sender);
            pl.sendTitle(applyToBuilder(builder, t).build());
            context.sendMessage("command.title.player.success.single", this.type, t, pl.getName());
        }
        return context.successResult();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.titleConfig = serviceCollection.configProvider().getModuleConfig(NotificationConfig.class).getTitleDefaults();
    }

    private int toTicks(final ICommandContext source, final TextComponent key, final Supplier<Double> supplier) {
        return (int) (Math.max(0.05, source.getOne(key.toPlainSingle(), double.class).orElseGet(supplier)) * 20.0);
    }

    protected abstract Title.Builder applyToBuilder(Title.Builder builder, TextComponent text);

}
