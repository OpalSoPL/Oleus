/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentKeys;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.storage.dataobjects.keyed.IKeyedDataObject;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

@Command(
        aliases = {"lockweather", "killweather" },
        basePermission = EnvironmentPermissions.BASE_LOCKWEATHER,
        commandDescriptionKey = "lockweather",
        async = true
)
public class LockWeatherCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection),
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<WorldProperties> world = context.getWorldPropertiesOrFromSelf(NucleusParameters.Keys.WORLD);
        if (!world.isPresent()) {
            return context.errorResult("command.specifyworld");
        }

        final WorldProperties wp = world.get();
        try (final IKeyedDataObject.Value<Boolean> vb = context.getServiceCollection().storageManager()
                .getOrCreateWorldOnThread(wp.getUniqueId())
                .getAndSet(EnvironmentKeys.LOCKED_WEATHER)) {
            final boolean current = vb.getValue().orElse(false);
            final boolean toggle = context.getOne(NucleusParameters.Keys.BOOL, Boolean.class).orElse(!current);
            vb.setValue(toggle);
            if (toggle) {
                context.sendMessage("command.lockweather.locked", wp.getWorldName());
            } else {
                context.sendMessage( "command.lockweather.unlocked", wp.getWorldName());
            }
        }

        return context.successResult();
    }
}
