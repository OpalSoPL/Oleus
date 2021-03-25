/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.commands;

import io.github.nucleuspowered.nucleus.api.module.nickname.exception.NicknameException;
import io.github.nucleuspowered.nucleus.modules.nickname.NicknamePermissions;
import io.github.nucleuspowered.nucleus.modules.nickname.services.NicknameService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;

@Command(
        aliases = {"nick", "nickname"},
        basePermission = NicknamePermissions.BASE_NICK,
        commandDescriptionKey = "nick",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = NicknamePermissions.EXEMPT_COOLDOWN_NICK),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = NicknamePermissions.EXEMPT_WARMUP_NICK),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = NicknamePermissions.EXEMPT_COST_NICK)
        },
        associatedPermissions = {
                NicknamePermissions.OTHERS_NICK,
                NicknamePermissions.NICKNAME_COLOUR,
                NicknamePermissions.NICKNAME_STYLE
        }
)
@EssentialsEquivalent(value = {"nick", "nickname"}, isExact = false,
        notes = "To remove a nickname, use '/delnick'")
public class NicknameCommand implements ICommandExecutor {

    private final Parameter.Value<Component> nicknameParameter = Parameter.formattingCodeText().key("nickname").build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier()
                    .createOnlyOtherUserPermissionElement(NicknamePermissions.OTHERS_NICK),
                this.nicknameParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User pl = context.getUserFromArgs();
        final Component name = context.requireOne(this.nicknameParameter);

        try {
            context.getServiceCollection().getServiceUnchecked(NicknameService.class).setNick(pl.uniqueId(), name, false);
        } catch (final NicknameException e) {
            return context.errorResultLiteral(e.componentMessage());
        }

        if (!context.is(pl)) {
            context.sendMessageText(
                    LinearComponents.linear(
                            context.getMessage("command.nick.success.other", pl.name()),
                            Component.text(" - "),
                            name.color(NamedTextColor.WHITE)
                    ));
        }

        return context.successResult();
    }

}
