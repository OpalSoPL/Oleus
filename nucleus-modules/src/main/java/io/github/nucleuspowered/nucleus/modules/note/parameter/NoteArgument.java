/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.parameter;

import io.github.nucleuspowered.nucleus.modules.note.data.NoteData;
import io.github.nucleuspowered.nucleus.modules.note.services.NoteHandler;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class NoteArgument extends CommandElement {

    private final NoteHandler handler;
    private final IMessageProviderService messageProviderService;

    public NoteArgument(@Nullable final TextComponent key, final NoteHandler handler, final IMessageProviderService messageProviderService) {
        super(key);
        this.handler = handler;
        this.messageProviderService = messageProviderService;
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final Optional<String> optPlayer = args.nextIfPresent();
        if (!optPlayer.isPresent()) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.note.nouserarg"));
        }
        final String player = optPlayer.get();

        final Optional<User> optUser = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(player);
        if (!optUser.isPresent()) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.note.nouser", player));
        }
        final User user = optUser.get();

        final Optional<String> optIndex = args.nextIfPresent();
        if (!optIndex.isPresent()) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.note.noindex", user.getName()));
        }

        final List<NoteData> noteData = this.handler.getNotesInternal(user);
        final int index;
        try {
            index = Integer.parseInt(optIndex.get()) - 1;
            if (index >= noteData.size() || index < 0) {
                throw args.createError(this.messageProviderService.getMessageFor(source, "args.note.nonotedata", optIndex.get(), user.getName()));
            }
        } catch (final NumberFormatException ex) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.note.indexnotnumber"));
        }

        return new Result(user, noteData.get(index));

    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return Collections.emptyList();
    }

    @Override
    public TextComponent getUsage(final CommandSource src) {
        return Text.of("<user> <ID>");
    }

    public static class Result {
        public final User user;
        public final NoteData noteData;

        Result(final User user, final NoteData noteData) {
            this.user = user;
            this.noteData = noteData;
        }
    }
}
