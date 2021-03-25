/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.command;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.modules.notification.config.NotificationConfig;
import io.github.nucleuspowered.nucleus.modules.notification.config.TitleConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public abstract class TitleBase implements ICommandExecutor, IReloadableService.Reloadable {

    private final String multiplePerm;
    private final String type;
    private final Parameter.Value<Double> fadeIn = Parameter.builder(Double.class)
            .key("fade in")
            .addParser(VariableValueParameters.doubleRange().setMin(0.0).build())
            .build();
    private final Parameter.Value<Double> fadeOut = Parameter.builder(Double.class)
            .key("fade out")
            .addParser(VariableValueParameters.doubleRange().setMin(0.0).build())
            .build();
    private final Parameter.Value<Double> timeOnScreen = Parameter.builder(Double.class)
            .key("time on screen")
            .addParser(VariableValueParameters.doubleRange().setMin(0.0).build())
            .build();

    private TitleConfig titleConfig = new TitleConfig();

    protected TitleBase(final String multiplePerm, final String type) {
        this.multiplePerm = multiplePerm;
        this.type = type;
    }

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of(NucleusParameters.MANY_PLAYER, "p"),
                Flag.of(this.fadeIn, "i"),
                Flag.of(this.fadeOut, "o"),
                Flag.of(this.timeOnScreen, "t")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // If we don't have a player, check we can send to all.
        final Collection<ServerPlayer> targets =
                context.getOne(NucleusParameters.MANY_PLAYER)
                        .map(Collections::unmodifiableCollection)
                        .orElseGet(Sponge.server()::getOnlinePlayers);

        if (targets.isEmpty()) {
            return context.errorResult("command.title.noonline");
        }

        if (targets.size() > 1 && !context.testPermission(this.multiplePerm)) {
            return context.errorResult("command.title.multi.noperms");
        }

        final String message = context.requireOne(NucleusParameters.MESSAGE);
        final NucleusTextTemplate textTemplate =
                context.getServiceCollection().textTemplateFactory().createFromAmpersandString(message);
        final Title.Times times = Title.Times.of(
                this.toDuration(context, this.fadeIn, this.titleConfig::getFadeIn),
                this.toDuration(context, this.timeOnScreen, this.titleConfig::getTime),
                this.toDuration(context, this.fadeOut, this.titleConfig::getFadeOut)
        );

        final Object sender = context.getCommandSourceRoot();
        if (targets.size() > 1) {
            for (final ServerPlayer pl : targets) {
                pl.showTitle(this.createTitle(textTemplate.getForObjectWithSenderToken(pl, sender), times));
            }
            context.sendMessage("command.title.player.success.multi", this.type, targets.size());
        } else {
            final ServerPlayer pl = targets.iterator().next();
            final Component t = textTemplate.getForObjectWithSenderToken(pl, sender);
            pl.showTitle(this.createTitle(textTemplate.getForObjectWithSenderToken(pl, sender), times));
            context.sendMessage("command.title.player.success.single", this.type, t, pl.name());
        }
        return context.successResult();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.titleConfig = serviceCollection.configProvider().getModuleConfig(NotificationConfig.class).getTitleDefaults();
    }

    private Duration toDuration(final ICommandContext source, final Parameter.Value<Double> key, final Supplier<Double> supplier) {
        return Duration.ofMillis((long) (1000.0 * source.getOne(key).orElseGet(supplier)));
    }

    protected abstract Title createTitle(Component text, Title.Times times);

}
