/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.misc.MiscPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@Command(
        aliases = {"feed", "eat"},
        basePermission = MiscPermissions.BASE_FEED,
        commandDescriptionKey = "feed",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MiscPermissions.EXEMPT_COOLDOWN_FEED),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MiscPermissions.EXEMPT_WARMUP_FEED),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MiscPermissions.EXEMPT_COST_FEED)
        },
        associatedPermissions = MiscPermissions.OTHERS_FEED
)
@EssentialsEquivalent({"feed", "eat"})
public class FeedCommand implements ICommandExecutor {

    private final Parameter.Value<ServerPlayer> parameter;

    @Inject
    public FeedCommand(final INucleusServiceCollection serviceCollection) {
        this.parameter = serviceCollection.commandElementSupplier()
                .createOnlyOtherPlayerPermissionElement(MiscPermissions.OTHERS_FEED);
    }

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.parameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer pl = context.getPlayerFromArgs();
        // Get the food data and modify it.
        pl.offer(Keys.EXHAUSTION, pl.get(Keys.MAX_EXHAUSTION).orElse(40.0));
        pl.offer(Keys.SATURATION, pl.get(Keys.MAX_SATURATION).orElse(40.0));
        if (pl.offer(Keys.FOOD_LEVEL, pl.get(Keys.MAX_FOOD_LEVEL).orElse(20)).isSuccessful()) {
            context.sendMessageTo(pl, "command.feed.success.self");
            if (!context.is(pl)) {
                context.sendMessage("command.feed.success.other", pl.getName());
            }

            return context.successResult();
        } else {
            return context.errorResult("command.feed.error");
        }
    }
}
