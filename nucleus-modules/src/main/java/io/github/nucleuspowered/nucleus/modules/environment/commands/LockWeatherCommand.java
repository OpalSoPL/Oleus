/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentKeys;
import io.github.nucleuspowered.nucleus.modules.environment.EnvironmentPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.storage.dataobjects.keyed.IKeyedDataObject;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

@Command(
        aliases = {"lockweather", "killweather" },
        basePermission = EnvironmentPermissions.BASE_LOCKWEATHER,
        commandDescriptionKey = "lockweather")
public class LockWeatherCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY,
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<WorldProperties> world = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.getKey());
        if (!world.isPresent()) {
            return context.errorResult("command.specifyworld");
        }

        final WorldProperties wp = world.get();
        try (final IKeyedDataObject.Value<Boolean> vb = context.getServiceCollection().storageManager()
                .getOrCreateWorldOnThread(wp.getKey())
                .getAndSet(EnvironmentKeys.LOCKED_WEATHER)) {
            final boolean current = vb.getValue().orElse(false);
            final boolean toggle = context.getOne(NucleusParameters.ONE_TRUE_FALSE).orElse(!current);
            vb.setValue(toggle);
            if (toggle) {
                context.sendMessage("command.lockweather.locked", wp.getKey().asString());
            } else {
                context.sendMessage( "command.lockweather.unlocked", wp.getKey().asString());
            }
        }

        return context.successResult();
    }
}
