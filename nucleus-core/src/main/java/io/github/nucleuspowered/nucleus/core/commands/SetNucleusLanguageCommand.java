/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;

import java.util.Locale;

@Command(
        aliases = { "setnucleuslanguage", "setnuclang" },
        basePermission = CorePermissions.BASE_NUSERPREFS,
        commandDescriptionKey = "setnucleuslanguage",
        associatedPermissions = CorePermissions.OTHERS_SETNUCLEUSLANGUAGE
)
public class SetNucleusLanguageCommand implements ICommandExecutor {

    private final IUserPreferenceService preferenceService;
    private final IPlayerDisplayNameService displayNameService;

    private final Parameter.Value<Locale> localeParameter;
    private final Parameter.Value<User> userParameter;

    @Inject
    public SetNucleusLanguageCommand(final INucleusServiceCollection serviceCollection) {
        this.preferenceService = serviceCollection.userPreferenceService();
        this.displayNameService = serviceCollection.playerDisplayNameService();

        this.userParameter = Parameter.user().setRequiredPermission(CorePermissions.OTHERS_SETNUCLEUSLANGUAGE).setKey("user").build();
        this.localeParameter = serviceCollection.commandElementSupplier().createLocaleElement("locale");
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] { this.userParameter, this.localeParameter };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User target = context.getUserFromArgs(this.userParameter);
        Locale locale = context.requireOne(this.localeParameter);
        // This should exist...
        final NucleusUserPreferenceService.PreferenceKey<Locale> preferenceKey = this.preferenceService.keys().playerLocale().get();

        if (locale.toString().isEmpty()) {
            this.preferenceService.removePreferenceFor(target.getUniqueId(), preferenceKey);
            locale = Locale.UK;
        } else {
            this.preferenceService.setPreferenceFor(target.getUniqueId(), preferenceKey, locale);
        }

        if (!context.is(target)) {
            context.sendMessage("command.setnucleuslang.success.other",
                    this.displayNameService.getDisplayName(target),
                    locale.toString(),
                    locale.getDisplayName());
        }

        context.sendMessageTo(
                context.getAudience(),
                "command.setnucleuslang.success.self",
                locale.toString(),
                locale.getDisplayName()
        );

        return context.successResult();
    }
}
