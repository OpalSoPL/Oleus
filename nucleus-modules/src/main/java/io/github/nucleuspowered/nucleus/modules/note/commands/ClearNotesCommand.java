/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.commands;

import io.github.nucleuspowered.nucleus.modules.note.NotePermissions;
import io.github.nucleuspowered.nucleus.modules.note.data.NoteData;
import io.github.nucleuspowered.nucleus.modules.note.services.NoteHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import java.util.List;

@Command(
        aliases = {"clearnotes", "removeallnotes"},
        basePermission = NotePermissions.BASE_CLEARNOTES,
        commandDescriptionKey = "clearnotes")
public class ClearNotesCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_USER.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.requireOne(NucleusParameters.Keys.USER, User.class);
        final NoteHandler handler = context.getServiceCollection().getServiceUnchecked(NoteHandler.class);

        final List<NoteData> notes = handler.getNotesInternal(user);
        if (notes.isEmpty()) {
            context.sendMessage("command.checknotes.none", user.getName());
            return context.successResult();
        }

        if (handler.clearNotes(user)) {
            context.sendMessage("command.clearnotes.success", user.getName());
            return context.successResult();
        }

        return context.errorResult("command.clearnotes.failure", user.getName());
    }
}