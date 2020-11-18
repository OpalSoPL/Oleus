/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.mail.NucleusMailService;
import io.github.nucleuspowered.nucleus.api.module.mail.data.MailMessage;
import io.github.nucleuspowered.nucleus.modules.mail.data.MailData;
import io.github.nucleuspowered.nucleus.modules.mail.services.MailHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Identifiable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MailReadBase {

    public static MailReadBase INSTANCE = new MailReadBase();

    private MailReadBase() {}

    static final String FILTERS = "filters";

    public ICommandResult executeCommand(final ICommandContext context,
            final User target,
            final Collection<NucleusMailService.MailFilter> lmf) throws CommandException {
        final MailHandler handler = context.getServiceCollection().getServiceUnchecked(MailHandler.class);
        final List<MailMessage> lmd;
        if (!lmf.isEmpty()) {
            lmd = handler.getMailInternal(target, lmf.toArray(new NucleusMailService.MailFilter[0]));
        } else {
            lmd = handler.getMailInternal(target);
        }

        if (lmd.isEmpty()) {
            if (context.is(target)) {
                context.sendMessage(!lmf.isEmpty() ? "command.mail.none.filter" : "command.mail.none.normal.self");
            } else {
                context.sendMessage(!lmf.isEmpty() ? "command.mail.none.filter" : "command.mail.none.normal.other", target.getName());
            }

            return context.successResult();
        }

        final List<Text> mails = lmd.stream().sorted(Comparator.comparing(MailMessage::getDate))
                        .map(x -> createMessage(context, x, target)).collect(Collectors.toList());

        // Paginate the mail.
        final PaginationList.Builder b = Util.getPaginationBuilder(context.getCommandSourceRoot()).padding(Text.of(TextColors.GREEN, "-")).title(
                getHeader(context, target, !lmf.isEmpty())).contents(mails);
        if (!context.is(Player.class)) {
            b.linesPerPage(-1);
        } else {
            b.header(context.getMessage("mail.header"));
        }

        b.sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }

    private TextComponent getHeader(final ICommandContext context, final User user, final boolean isFiltered) {
        if (context.is(user)) {
            return context.getMessage(isFiltered ? "mail.title.filter.self" : "mail.title.nofilter.self");
        }

        return  context.getMessage(isFiltered ? "mail.title.filter.other" : "mail.title.nofilter.other", user.getName());
    }

    private TextComponent createMessage(final ICommandContext context, final MailMessage md, final User user) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault());
        final UUID uuid = getUuid(md);
        final TextComponent name = context.getServiceCollection().playerDisplayNameService().getDisplayName(uuid);
        return name.toBuilder().color(TextColors.GREEN).style(TextStyles.UNDERLINE)
                        .onHover(TextActions.showText(context.getMessage("command.mail.hover")))
                        .onClick(TextActions.executeCallback(src -> {
                            src.sendMessage(Text.builder().append(context.getMessage("command.mail.date"))
                                    .append(Text.of(" ", TextColors.WHITE, dtf.format(md.getDate()))).build());
                            final Text.Builder tb = Text.builder().append(context.getMessage("command.mail.sender"))
                                    .append(Text.of(" ", TextColors.WHITE, name))
                                    .append(Text.of(TextColors.YELLOW, " - "));

                            // If the sender is not the server, allow right of reply.
                            if (!uuid.equals(Util.CONSOLE_FAKE_UUID)) {
                                tb.append(context.getMessage("standard.reply").toBuilder().color(TextColors.GREEN)
                                        .onHover(TextActions.showText(context.getMessage("command.mail.reply.label", name)))
                                        .onClick(TextActions.suggestCommand("/mail send " + name + " ")).build())
                                        .append(Text.of(TextColors.YELLOW, " - "));
                            }

                            src.sendMessage(tb.append(context.getMessage("standard.delete").toBuilder().color(TextColors.RED)
                                .onHover(TextActions.showText(context.getMessage("command.mail.delete.label")))
                                .onClick(TextActions.executeCallback(s -> {
                                    if (context.getServiceCollection().getServiceUnchecked(MailHandler.class).removeMail(user, md)) {
                                        context.sendMessage("command.mail.delete.success");
                                    } else {
                                        context.sendMessage("command.mail.delete.fail");
                                    }
                                })).build()).build());
                            context.sendMessage("command.mail.message");
                            src.sendMessage(Text.of(TextColors.WHITE, md.getMessage()));
                        })).append(Text.of(": " + md.getMessage())).build();
    }

    private UUID getUuid(final MailMessage message) {
        if (message instanceof MailData) {
            return ((MailData) message).getUuid();
        } else {
            return message.getSender().map(Identifiable::getUniqueId).orElse(Util.CONSOLE_FAKE_UUID);
        }
    }

}
