/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.modules.admin.AdminPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.parameter.Parameter;
import java.util.Optional;

@Command(aliases = {"stop"},
        basePermission = AdminPermissions.BASE_STOP,
        commandDescriptionKey = "stop")
public class StopCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) {
        final Optional<? extends String> opt = context.getOne(NucleusParameters.OPTIONAL_MESSAGE);
        if (opt.isPresent()) {
            Sponge.server().shutdown(LegacyComponentSerializer.legacyAmpersand().deserialize(opt.get()));
        } else {
            Sponge.server().shutdown();
        }

        return context.successResult();
    }
}
