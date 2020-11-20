/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = { "rename" },
        basePermission = KitPermissions.BASE_KIT_RENAME,
        commandDescriptionKey = "kit.rename",
        parentCommand = KitCommand.class
)
public class KitRenameCommand implements ICommandExecutor {

    private final Parameter.Value<String> target = Parameter.string()
            .setKey("target name")
            .build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission(),
                this.target
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        try {
            final String name1 = context.requireOne(KitService.KIT_KEY).getName();
            final String name2 = context.requireOne(this.target);
            context.getServiceCollection().getServiceUnchecked(KitService.class).renameKit(name1, name2);
            context.sendMessage("command.kit.rename.renamed", name1, name2);
            return context.successResult();
        } catch (final IllegalArgumentException e) {
            return context.errorResultLiteral(Component.text(e.getMessage(), NamedTextColor.RED));
        }
    }
}
