/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.parameter;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.mail.NucleusMailService;
import io.github.nucleuspowered.nucleus.modules.mail.services.MailHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Identifiable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class MailFilterArgument extends CommandElement {

    private static final Pattern late = Pattern.compile("b:(\\d+)");
    private static final Pattern early = Pattern.compile("a:(\\d+)");
    private final MailHandler handler;

    public MailFilterArgument(@Nullable final TextComponent key, final MailHandler handler) {
        super(key);
        this.handler = handler;
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        // Get all the arguments in list.
        final List<UUID> players = new ArrayList<>();
        boolean console = false;
        Instant ea = null;
        Instant l = null;
        final List<String> message = new ArrayList<>();
        while (args.hasNext()) {
            final String toParse = args.next();
            try {
                final String s = toParse.substring(0, 2);

                switch (s) {
                    case "p:":
                        player(toParse.split(":", 2)[1]).ifPresent(players::add);
                        break;
                    case "m:":
                        message.add(toParse.split(":", 2)[1]);
                        break;
                    case "c:":
                        console = true;
                        break;
                    case "b:":
                        final Matcher b = late.matcher(toParse);
                        if (b.find()) {
                            // Days before
                            l = Instant.now().minus(Integer.parseInt(b.group(1)), ChronoUnit.DAYS);
                        }

                        break;
                    case "a:":
                        final Matcher a = early.matcher(toParse);
                        if (a.find()) {
                            ea = Instant.now().minus(Integer.parseInt(a.group(1)), ChronoUnit.DAYS);
                        }

                        break;
                }
            } catch (final Exception e) {
                // ignored
            }
        }

        final List<NucleusMailService.MailFilter> lmf = new ArrayList<>();
        if (console || !players.isEmpty()) {
            lmf.add(this.handler.createSenderFilter(console, players));
        }

        if (ea != null || l != null) {
            lmf.add(this.handler.createDateFilter(ea, l));
        }

        if (!message.isEmpty()) {
            lmf.add(this.handler.createMessageFilter(false, message));
        }

        return lmf.isEmpty() ? null : lmf;
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return new ArrayList<>();
    }

    private Optional<UUID> player(final String text) {
        if (text.equalsIgnoreCase("server") || (text.equalsIgnoreCase("console"))) {
            return Optional.of(Util.CONSOLE_FAKE_UUID);
        }

        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        final Optional<User> ou = uss.get(text);
        return ou.map(Identifiable::getUniqueId);
    }

}
