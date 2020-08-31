/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.NucleusPluginInfo;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tristate;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Command(
        aliases = "info",
        basePermission = CorePermissions.BASE_NUCLEUS_INFO,
        commandDescriptionKey = "nucleus.info",
        parentCommand = NucleusCommand.class,
        async = true
)
public class InfoCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Sponge versions
        final List<String> information = Lists.newArrayList();

        final String separator = "------------";
        information.add(separator);
        information.add("Nucleus Diagnostics");
        information.add(separator);

        information.add("This file contains information about Nucleus and the environment it runs in.");

        information.add(separator);
        information.add("Environment");
        information.add(separator);

        final Platform platform = Sponge.getPlatform();
        final PluginContainer game = platform.getContainer(Platform.Component.GAME);
        final PluginContainer implementation = platform.getContainer(Platform.Component.IMPLEMENTATION);
        final PluginContainer api = platform.getContainer(Platform.Component.API);

        information.add(String.format("Minecraft Version: %s %s", game.getName(), game.getVersion().orElse("unknown")));
        information.add(String.format("Sponge Version: %s %s", implementation.getName(), implementation.getVersion().orElse("unknown")));
        information.add(String.format("Sponge API Version: %s %s", api.getName(), api.getVersion().orElse("unknown")));
        information.add("Nucleus Version: " + NucleusPluginInfo.VERSION + " (Git: " + NucleusPluginInfo.GIT_HASH + ")");

        information.add(separator);
        information.add("Plugins");
        information.add(separator);

        Sponge.getPluginManager().getPlugins().forEach(x -> information.add(x.getName() + " (" + x.getId() + ") version " + x.getVersion().orElse("unknown")));

        information.add(separator);
        information.add("Registered Commands");
        information.add(separator);

        final Map<String, String> commands = Maps.newHashMap();
        final Map<String, String> plcmds = Maps.newHashMap();
        final CommandManager manager = Sponge.getCommandManager();
        manager.getPrimaryAliases().forEach(x -> {
            final Optional<? extends CommandMapping> ocm = manager.get(x);
            if (ocm.isPresent()) {
                final Set<String> a = ocm.get().getAllAliases();
                final Optional<PluginContainer> optionalPC = manager.getOwner(ocm.get());
                if (optionalPC.isPresent()) {
                    final PluginContainer container = optionalPC.get();
                    final String id = container.getId();
                    final String info = " - " + container.getName() + " (" + id + ") version " + container.getVersion().orElse("unknown");
                    a.forEach(y -> {
                        if (y.startsWith(id + ":")) {
                            // /nucleus:<blah>
                            plcmds.put(y, "/" + y + info);
                        } else {
                            commands.put(y, "/" + y + info);
                        }
                    });
                } else {
                    final String info = " - unknown (plugin container not present)";
                    a.forEach(y -> commands.put(y, "/" + y + info));
                }
            } else {
                commands.put(x, "/" + x + " - unknown (mapping not present)");
            }
        });

        commands.entrySet().stream().sorted(Comparator.comparing(x -> x.getKey().toLowerCase())).forEachOrdered(x -> information.add(x.getValue()));
        information.add(separator);
        information.add("Namespaced commands");
        information.add(separator);
        plcmds.entrySet().stream().sorted(Comparator.comparing(x -> x.getKey().toLowerCase())).forEachOrdered(x -> information.add(x.getValue()));

        information.add(separator);
        information.add("Nucleus: Enabled Modules");
        information.add(separator);

        context.getServiceCollection().configProvider().getModules(Tristate.TRUE).stream().sorted().forEach(information::add);

        final Collection<String> disabled = context.getServiceCollection().configProvider().getModules(Tristate.FALSE);
        if (!disabled.isEmpty()) {
            information.add(separator);
            information.add("Nucleus: Disabled Modules");
            information.add(separator);

            disabled.stream().sorted().forEach(information::add);
        }


        final String fileName = "nucleus-info-" + DateTimeFormatter.BASIC_ISO_DATE.format(LocalDateTime.now()) + "-" + DateTimeFormatter.ofPattern("HHmmss").format(LocalDateTime.now()) + ".txt";
        try (final BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (final String s : information) {
                fw.write(s);
                fw.newLine();
            }

            fw.flush();
        } catch (final Exception e) {
            throw context.createException("command.nucleus.info.fileerror", e);
        }

        context.sendMessage("command.nucleus.info.saved", fileName);
        return context.successResult();
    }
}
