/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Locale;

import com.google.inject.Inject;

@Command(
        aliases = { "setnucleuslanguage", "setnuclang" },
        basePermission = CorePermissions.BASE_NUSERPREFS,
        commandDescriptionKey = "setnucleuslanguage",
        async = true,
        associatedPermissions = CorePermissions.OTHERS_SETNUCLEUSLANGUAGE
)
public class SetNucleusLanguageCommand implements ICommandExecutor {

    private final String LOCALE_ENTRY = "locale";
    private final IUserPreferenceService preferenceService;
    private final IPlayerDisplayNameService displayNameService;

    @Inject
    public SetNucleusLanguageCommand(final INucleusServiceCollection serviceCollection) {
        this.preferenceService = serviceCollection.userPreferenceService();
        this.displayNameService = serviceCollection.playerDisplayNameService();
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.commandElementSupplier()
                    .createOtherUserPermissionElement(false, CorePermissions.OTHERS_SETNUCLEUSLANGUAGE),
                serviceCollection.commandElementSupplier().createLocaleElement()
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User target = context.getUserFromArgs();
        Locale locale = context.requireOne(this.LOCALE_ENTRY, Locale.class);
        // This should exist...
        final NucleusUserPreferenceService.PreferenceKey<Locale> preferenceKey = this.preferenceService.keys().playerLocale().get();

        if (locale.toString().isEmpty()) {
            this.preferenceService.removePreferenceFor(target, preferenceKey);
            locale = Locale.UK;
        } else {
            this.preferenceService.setPreferenceFor(target, preferenceKey, locale);
        }

        if (!context.is(target)) {
            context.sendMessage("command.setnucleuslang.success.other",
                    this.displayNameService.getDisplayName(target),
                    locale.toString(),
                    locale.getDisplayName());
        }

        if (target instanceof MessageReceiver) {
            context.sendMessageTo(
                    (MessageReceiver) target,
                    "command.setnucleuslang.success.self",
                    locale.toString(),
                    locale.getDisplayName()
            );
        }

        return context.successResult();
    }
}
