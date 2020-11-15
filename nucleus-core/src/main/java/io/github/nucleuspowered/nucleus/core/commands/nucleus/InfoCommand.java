/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.IPluginInfo;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.interfaces.IModuleReporter;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.manager.CommandManager;
import org.spongepowered.plugin.PluginContainer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Command(
        aliases = "info",
        basePermission = CorePermissions.BASE_NUCLEUS_INFO,
        commandDescriptionKey = "nucleus.info",
        parentCommand = NucleusCommand.class,
        async = true
)
public final class InfoCommand implements ICommandExecutor {

    private final IPluginInfo pluginInfo;

    @Inject
    public InfoCommand(final IPluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

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

        information.add(String.format("Minecraft Version: %s %s", game.getMetadata().getName().orElse("unknown"), game.getMetadata().getVersion()));
        information.add(String.format("Sponge Version: %s %s", implementation.getMetadata().getName().orElse("unknown"),
                implementation.getMetadata().getVersion()));
        information.add(String.format("Sponge API Version: %s %s", api.getMetadata().getName().orElse("unknown"), api.getMetadata().getVersion()));
        information.add("Nucleus Version: " + this.pluginInfo.version() + " (Git: " + this.pluginInfo.gitHash() + ")");

        information.add(separator);
        information.add("Plugins");
        information.add(separator);

        Sponge.getPluginManager().getPlugins().forEach(x -> information.add(x.getMetadata().getName().orElse("unknown") + " (" + x.getMetadata().getId() + ") "
                + "version " + x.getMetadata().getVersion()));

        information.add(separator);
        information.add("Registered Commands");
        information.add(separator);

        final Map<String, String> commands = Maps.newHashMap();
        final Map<String, String> plcmds = Maps.newHashMap();
        final CommandManager manager = Sponge.getCommandManager();
        manager.getKnownAliases().stream()
                .map(x -> manager.getCommandMapping(x).orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .forEach(x -> {
                    final Set<String> a = x.getAllAliases();
                    final PluginContainer container = x.getPlugin();
                    final String id = container.getMetadata().getId();
                    final String info = " - " + container.getMetadata().getName() + " (" + id + ") version " + container.getMetadata().getVersion();
                    a.forEach(y -> {
                        if (y.startsWith(id + ":")) {
                            // /nucleus:<blah>
                            plcmds.put(y, "/" + y + info);
                        } else {
                            commands.put(y, "/" + y + info);
                        }
                    });
                });

        commands.entrySet().stream().sorted(Comparator.comparing(x -> x.getKey().toLowerCase())).forEachOrdered(x -> information.add(x.getValue()));
        information.add(separator);
        information.add("Namespaced commands");
        information.add(separator);
        plcmds.entrySet().stream().sorted(Comparator.comparing(x -> x.getKey().toLowerCase())).forEachOrdered(x -> information.add(x.getValue()));

        information.add(separator);
        information.add("Nucleus: Enabled Modules");
        information.add(separator);

        final IModuleReporter moduleReporter = context.getServiceCollection().moduleReporter();
        moduleReporter.enabledModules().stream().sorted().forEach(x -> information.add(x.getName() + " (" + x.getId() + ")"));

        final Collection<String> disabled =
                moduleReporter.discoveredModules().stream().filter(x -> !moduleReporter.isLoaded(x)).sorted().collect(Collectors.toList());
        if (!disabled.isEmpty()) {
            information.add(separator);
            information.add("Nucleus: Disabled Modules");
            information.add(separator);

            information.addAll(disabled);
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
