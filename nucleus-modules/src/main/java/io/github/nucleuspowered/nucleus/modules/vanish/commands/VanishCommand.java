/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.commands;

import io.github.nucleuspowered.nucleus.modules.vanish.VanishKeys;
import io.github.nucleuspowered.nucleus.modules.vanish.VanishPermissions;
import io.github.nucleuspowered.nucleus.modules.vanish.services.VanishService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.storage.dataobjects.keyed.IKeyedDataObject;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
@EssentialsEquivalent({"vanish", "v"})
@Command(
        aliases = {"vanish", "v"},
        basePermission = VanishPermissions.BASE_VANISH,
        commandDescriptionKey = "vanish",
        associatedPermissions = {
                VanishPermissions.VANISH_SEE,
                VanishPermissions.VANISH_PERSIST
        }
)
public class VanishCommand implements ICommandExecutor {

    private final String b = "toggle";

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.commandElementSupplier().createOtherUserPermissionElement(false, VanishPermissions.OTHERS_VANISH),
                GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.bool(Text.of(this.b))))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User ou = context.getUserFromArgs();
        if (ou.getPlayer().isPresent()) {
            return onPlayer(context, ou.getPlayer().get());
        }

        if (!context.testPermissionFor(ou, "persist")) {
            return context.errorResult("command.vanish.noperm", ou.getName());
        }

        final boolean result;
        try (final IKeyedDataObject.Value<Boolean> value = context
                .getServiceCollection()
                .storageManager()
                .getUserService()
                .getOrNewOnThread(ou.getUniqueId())
                .getAndSet(VanishKeys.VANISH_STATUS)) {
            result = context.getOne(this.b, Boolean.class).orElseGet(() -> !value.getValue().orElse(false));
            value.setValue(result);
            final VanishService service = context.getServiceCollection().getServiceUnchecked(VanishService.class);
            if (result) {
                service.vanishPlayer(ou);
            } else {
                service.unvanishPlayer(ou);
            }
        }

        context.sendMessage(
                "command.vanish.successuser",
                ou.getName(),
                result ? "loc:command.vanish.vanished" : "loc:command.vanish.visible");

        return context.successResult();
    }

    private ICommandResult onPlayer(final ICommandContext context, final Player playerToVanish) throws CommandException {
        if (playerToVanish.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.SPECTATOR)) {
            return context.errorResult("command.vanish.fail");
        }

        // If we don't specify whether to vanish, toggle
        final boolean toVanish = context.getOne(this.b, Boolean.class).orElse(!playerToVanish.get(Keys.VANISH).orElse(false));
        final VanishService service = context.getServiceCollection().getServiceUnchecked(VanishService.class);
        if (toVanish) {
            service.vanishPlayer(playerToVanish);
        } else {
            service.unvanishPlayer(playerToVanish);
        }

        context.sendMessageTo(
                playerToVanish,
                "command.vanish.success",
                toVanish ? "loc:command.vanish.vanished" : "loc:command.vanish.visible");

        if (!context.is(playerToVanish)) {
            context.sendMessage(
                    "command.vanish.successplayer",
                    context.getDisplayName(playerToVanish.getUniqueId()),
                    toVanish ? "loc:command.vanish.vanished" : "loc:command.vanish.visible");
        }

        return context.successResult();
    }
}
