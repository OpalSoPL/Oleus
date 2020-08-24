/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

@Command(
        aliases = { "nuserprefs", "userprefs" },
        basePermission = CorePermissions.BASE_NUSERPREFS,
        commandDescriptionKey = "nuserprefs",
        prefixAliasesWithN = false,
        async = true,
        associatedPermissions = CorePermissions.OTHERS_NUSERPREFS
)
public class NucleusUserPrefsCommand implements ICommandExecutor {

    private static final TextComponent SEPARATOR = Text.of(": ");
    private final IUserPreferenceService userPreferenceService;

    @Inject
    public NucleusUserPrefsCommand(final INucleusServiceCollection serviceCollection) {
        this.userPreferenceService = serviceCollection.userPreferenceService();
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.commandElementSupplier().createOtherUserPermissionElement(false, CorePermissions.OTHERS_NUSERPREFS),
                GenericArguments.optional(this.userPreferenceService.getElement())
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User target = context.getUserFromArgs();
        if (context.hasAny(UserPreferenceService.PREFERENCE_ARG.toPlain())) {
            // do we get or set?
            if (context.hasAny(UserPreferenceService.VALUE_ARG.toPlain())) {
                return this.set(context, target, context.is(target),
                        context.requireOne(UserPreferenceService.PREFERENCE_ARG.toPlain(), PreferenceKeyImpl.class),
                        context.requireOne(UserPreferenceService.VALUE_ARG.toPlain(), Object.class));
            } else {
                return this.get(context, target, context.requireOne(UserPreferenceService.PREFERENCE_ARG.toPlain(), PreferenceKeyImpl.class));
            }
        } else {
            return this.list(context, target);
        }

    }

    private <T> ICommandResult set(
            final ICommandContext context,
            final User target,
            final boolean isSelf,
            final PreferenceKeyImpl<T> key,
            @Nullable final Object value) {
        this.userPreferenceService.set(target.getUniqueId(), key, key.getValueClass().cast(value));
        if (isSelf) {
            context.sendMessage("command.userprefs.set.self", key.getID(), value);
        } else {
            context.sendMessage("command.userprefs.set.other", target, key.getID(), value);
        }
        return context.successResult();
    }

    private <T> ICommandResult get(final ICommandContext context, final User target, final PreferenceKeyImpl<T> key) throws CommandException {
        context.sendMessageText(
                this.get(context, context.getServiceCollection().userPreferenceService(),
                        key,
                        this.userPreferenceService.get(target.getUniqueId(), key).orElse(null)));
        return context.successResult();
    }

    private ICommandResult list(final ICommandContext context, final User target) throws CommandException {
        final Map<NucleusUserPreferenceService.PreferenceKey<?>, Object> ret = this.userPreferenceService.get(target);

        final List<Text> entry = new ArrayList<>();
        for (final Map.Entry<NucleusUserPreferenceService.PreferenceKey<?>, Object> e : ret.entrySet()) {
            final NucleusUserPreferenceService.PreferenceKey<?> key = e.getKey();
            final Object value = e.getValue();
            entry.add(this.get(context, this.userPreferenceService, key, value));
        }

        Util.getPaginationBuilder(context.getCommandSourceRoot())
                .title(context.getServiceCollection().messageProvider().getMessageFor(
                        context.getCommandSourceRoot(), "command.userprefs.title", target.getName()))
            .contents(entry).build().sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }

    private TextComponent get(final ICommandContext context,
            final IUserPreferenceService userPreferenceService,
            final NucleusUserPreferenceService.PreferenceKey<?> key,
            @Nullable final Object value) throws CommandException {
        final Text.Builder tb = Text.builder(key.getID().replaceAll("^nucleus:", ""));
        tb.append(SEPARATOR);
        final TextComponent result;
        final CommandSource commandSource = context.getCommandSourceRoot();
        if (value == null) {
            result = context.getServiceCollection().messageProvider().getMessageFor(commandSource, "standard.unset");
        } else if (value instanceof Boolean) {
            result = context.getServiceCollection().messageProvider().getMessageFor(commandSource, "standard." + (boolean) value);
        } else {
            result = Text.of(value);
        }

        tb.append(result);
        final String desc = userPreferenceService.getDescription(key);
        if (desc != null && !desc.isEmpty()) {
            tb.onHover(TextActions.showText(
                    key instanceof PreferenceKeyImpl ?
                        context.getServiceCollection().messageProvider()
                                .getMessageFor(commandSource, ((PreferenceKeyImpl<?>) key).getDescriptionKey()) :
                        Text.of(desc)));
        }
        return tb.build();
    }

}
