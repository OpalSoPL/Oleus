/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.manager.CommandMapping;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Command(
        aliases = "commandinfo",
        basePermission = CorePermissions.BASE_COMMANDINFO,
        commandDescriptionKey = "commandinfo",
        hasHelpCommand = false
)
public class CommandInfoCommand implements ICommandExecutor {

    private final Parameter.Value<CommandMapping> commandParameter;

    public CommandInfoCommand(final IMessageProviderService messageProviderService) {
        this.commandParameter = Parameter.builder(CommandMapping.class)
                .setKey("command")
                .parser(new CommandChoicesArgument(messageProviderService))
                .build();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.commandParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // we have the command, get the mapping
        final CommandMapping mapping = context.requireOne(this.commandParameter);

        final IMessageProviderService provider = context.getServiceCollection().messageProvider();
        final Component header = provider.getMessageFor(source, "command.commandinfo.title", mapping.getPrimaryAlias());

        final List<Component> content = new ArrayList<>();

        // Owner
        content.add(provider.getMessageFor(source, "command.commandinfo.owner", Sponge.getCommandManager().getOwner(mapping)
                .map(x -> x.getName() + " (" + x.getId() + ")")
                .orElseGet(() -> provider.getMessageString(source, "standard.unknown"))));
        content.add(provider.getMessageFor(source,"command.commandinfo.aliases", String.join(", ", mapping.getAllAliases())));

        if (mapping.getCallable() instanceof CommandControl) {
            this.nucleusCommand(content, context, provider, (CommandControl) mapping.getCallable());
        } else if (mapping.getCallable() instanceof CommandSpec) {
            this.specCommand(content, context, provider, mapping.getPrimaryAlias(), (CommandSpec) mapping.getCallable());
        } else {
            this.lowCommand(content, context, provider, mapping.getPrimaryAlias(), mapping.getCallable());
        }

        Util.getPaginationBuilder(context.is(Player.class))
                .title(header)
                .contents(content)
                .sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }

    private void nucleusCommand(
            final List<Text> content,
            final ICommandContext context,
            final IMessageProviderService provider,
            final CommandControl abstractCommand) throws CommandException{
        final CommandSource source = context.getCommandSourceRoot();
        content.add(provider.getMessageFor(source, "command.commandinfo.type", "loc:command.commandinfo.nucleus"));
        content.add(Text.EMPTY);
        final Component text = abstractCommand.getUsageText(source);
        if (text.isEmpty()) {
            content.add(provider.getMessageFor(source, "command.commandinfo.noinfo"));
        } else {
            content.add(text);
        }
    }

    private void specCommand(final List<Text> content,
            final ICommandContext context,
            final IMessageProviderService provider,
            final String alias,
            final CommandSpec spec) throws CommandException{ //List<Text> content, CommandSource source, MessageProvider provider, String
                                                                     // alias, CommandSpec spec) {
        final CommandSource src = context.getCommandSourceRoot();
        content.add(provider.getMessageFor(src, "command.commandinfo.type", "loc:command.commandinfo.spec"));
        final CommandExecutor executor = spec.getExecutor();
        if (executor instanceof ChildCommandElementExecutor) {
            try {
                content.add(provider.getMessageFor(src, "command.commandinfo.haschildren"));
                final Field field = ChildCommandElementExecutor.class.getDeclaredField("fallbackExecutor");
                field.setAccessible(true);
                content.add(provider.getMessageFor(src, "command.commandinfo.execclass", field.get(executor).getClass().getName()));
            } catch (final NoSuchFieldException | IllegalAccessException e) {
                content.add(provider.getMessageFor(src, "command.commandinfo.execclass", "loc:standard.unknown"));
            }
        }

        content.add(Text.EMPTY);
        content.add(provider.getMessageFor(src, "command.commandinfo.description"));

        spec.getShortDescription(src).ifPresent(x -> {
            content.add(provider.getMessageFor(src, "command.commandinfo.shortdescription"));
            content.add(x);
            content.add(Text.EMPTY);
        });
        spec.getExtendedDescription(src).ifPresent(x -> {
            content.add(provider.getMessageFor(src, "command.commandinfo.description"));
            content.add(x);
            content.add(Text.EMPTY);
        });

        content.add(provider.getMessageFor(src, "command.commandinfo.usage"));
        content.add(Text.of("/", alias, " ", spec.getUsage(src)));
    }

    private void lowCommand(
            final List<Text> content,
            final ICommandContext context,
            final IMessageProviderService provider,
            final String alias,
            final CommandCallable callable) throws CommandException {
        final CommandSource src = context.getCommandSourceRoot();
        content.add(provider.getMessageFor(src, "command.commandinfo.type", "loc:command.commandinfo.callable"));
        content.add(Text.EMPTY);

        callable.getShortDescription(src).ifPresent(x -> {
            content.add(provider.getMessageFor(src, "command.commandinfo.shortdescription"));
            content.add(x);
            content.add(Text.EMPTY);
        });
        callable.getHelp(src).ifPresent(x -> {
            content.add(provider.getMessageFor(src, "command.commandinfo.description"));
            content.add(x);
            content.add(Text.EMPTY);
        });

        content.add(provider.getMessageFor(src, "command.commandinfo.usage"));
        content.add(Text.of("/", alias, " ", callable.getUsage(src)));
    }

    private static class CommandChoicesArgument implements ValueParameter<CommandMapping> {

        private final IMessageProviderService messageProviderService;

        CommandChoicesArgument(final IMessageProviderService messageProviderService) {
            this.messageProviderService = messageProviderService;
        }

        @Override
        public List<String> complete(final CommandContext context) {
            return new ArrayList<>(Sponge.getCommandManager().suggest(context.getSubject(), context.getCause().getAudience(), ""));
        }

        @Override
        public Optional<? extends CommandMapping> getValue(
                final Parameter.@NonNull Key<? super CommandMapping> parameterKey,
                final ArgumentReader.@NonNull Mutable reader,
                final CommandContext.@NonNull Builder context) throws ArgumentParseException {
            final String next = reader.parseString();
            final Optional<CommandMapping> commandMapping = Sponge.getCommandManager().getCommandMapping(next);
            if (commandMapping.filter(x -> x.getRegistrar().canExecute(context.getCause(), x)).isPresent()) {
                return commandMapping;
            }
            throw reader.createException(this.messageProviderService.getMessageFor(context.getCause().getAudience(), "command.commandinfo.nocommand", next));
        }
    }
}
