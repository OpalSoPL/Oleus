/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.commandmetadata;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.guice.ConfigDirectory;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandInterceptor;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandMetadata;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.ICommandMetadataService;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigurateHelper;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.util.Action;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class CommandMetadataService implements ICommandMetadataService, IReloadableService.Reloadable {

    private static final String ROOT_ALIASES = "root level aliases";
    private static final String ENABLED = "enabled";
    private static final TypeToken<Map<String, Boolean>> MAP_TYPE_TOKEN = new TypeToken<Map<String, Boolean>>() {};

    private final Map<String, String> commandremap = new HashMap<>();
    private final Path commandsFile;
    private final IConfigurateHelper configurateHelper;
    private final IMessageProviderService messageProviderService;
    private final IReloadableService reloadableService;
    private final Map<String, CommandMetadata> commandMetadataMap = new HashMap<>();
    private final Map<CommandControl, List<String>> controlToAliases = new HashMap<>();
    private final BiMap<Class<? extends ICommandExecutor>, CommandControl> controlToExecutorClass = HashBiMap.create();

    private CommentedConfigurationNode commandsConfConfigNode;
    private boolean registrationComplete = false;
    private final List<ICommandInterceptor> interceptors = new ArrayList<>();
    private final List<String> registeredAliases = new ArrayList<>();
    private final Map<CommandMetadata, CommandControl> registeredCommands = new HashMap<>();

    @Inject
    public CommandMetadataService(@ConfigDirectory final Path configDirectory,
            final IReloadableService reloadableService,
            final IMessageProviderService messageProviderService,
            final IConfigurateHelper helper) {
        reloadableService.registerReloadable(this);
        this.configurateHelper = helper;
        this.messageProviderService = messageProviderService;
        this.reloadableService = reloadableService;
        this.commandsFile = configDirectory.resolve("commands.conf");
    }

    private String getKey(final Command command) {
        return this.getKey(new LinkedHashSet<>(), new StringBuilder(), command).toString().toLowerCase();
    }

    private StringBuilder getKey(
            final LinkedHashSet<Class<? extends ICommandExecutor>> traversed,
            final StringBuilder stringBuilder,
            final Command command) {
        if (command.parentCommand() != ICommandExecutor.class) {
            if (!traversed.add(command.parentCommand())) {
                final List<String> elements = new ArrayList<>();
                for (final Class<?> c : traversed) {
                    elements.add(c.getName());
                }
                throw new IllegalStateException("Circularity detected: " + System.lineSeparator() +
                        String.join(System.lineSeparator(), elements));
            }

            this.getKey(traversed, stringBuilder, command.parentCommand().getAnnotation(Command.class)).append(".");
        }

        return stringBuilder.append(command.aliases()[0]);
    }

    @Override
    public void registerCommands(
            final String id,
            final String name,
            final Collection<? extends Class<? extends ICommandExecutor>> associatedContext) {
        for (final Class<? extends ICommandExecutor> c : associatedContext) {
            this.registerCommand(id, name, c);
        }
    }

    @Override
    public void registerCommand(
            final String id,
            final String name,
            final Class<? extends ICommandExecutor> associatedContext) {
        Preconditions.checkState(!this.registrationComplete, "Registration has completed.");
        final Command command = associatedContext.getAnnotation(Command.class);
        if (command == null) {
            throw new NullPointerException("Command annotation is missing");
        }
        final String key = this.getKey(command);
        this.commandMetadataMap.put(key, new CommandMetadata(
                id,
                name,
                command,
                associatedContext,
                key,
                associatedContext.getAnnotation(EssentialsEquivalent.class)
        ));
    }

    /**
     * This is where the magic happens with registering commands. We need to:
     *
     * <ol>
     *     <li>Update command.conf</li>
     *     <li>Sift through and get the aliases to register.</li>
     *     <li>Register "root" aliases</li>
     *     <li>Then subcommands... obviously.</li>
     * </ol>
     */
    @Override
    public void completeRegistrationPhase(final INucleusServiceCollection serviceCollection,
            final RegisterCommandEvent<org.spongepowered.api.command.Command.Parameterized> event) {
        if (this.registrationComplete) {
            this.registeredCommands.values().forEach(x -> x.completeRegistration(serviceCollection));
            return;
        }
        this.registrationComplete = true;
        this.load();

        final Map<Class<? extends ICommandExecutor>, String> metadataStringMap = this.commandMetadataMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getExecutor(),
                        Map.Entry::getKey
                ));

        final Map<CommandMetadata, CommandControl> commands = new HashMap<>();
        // this.commandMetadataMap.values().forEach(metadata -> execToMeta.put(metadata.getExecutor(), metadata));

        final Map<Class<? extends ICommandExecutor>, Map<String, CommandMetadata>> toRegister = new HashMap<>();
        toRegister.put(ICommandExecutor.class, new HashMap<>());

        // We need aliases out
        this.mergeAliases();

        for (final CommandMetadata metadata : this.commandMetadataMap.values()) {
            // Only do this if it's enabled.
            final CommentedConfigurationNode commandNode = this.commandsConfConfigNode.getNode(metadata.getCommandKey());
            if (commandNode.getNode(ENABLED).getBoolean(false)) {
                // Get the aliases
                try {
                    final Map<String, Boolean> m = commandNode
                            .getNode(ROOT_ALIASES)
                            .getValue(MAP_TYPE_TOKEN);
                    if (m != null) {
                            m.entrySet()
                                .stream()
                                .filter(Map.Entry::getValue)
                                .map(Map.Entry::getKey)
                                .forEach(x -> toRegister.computeIfAbsent(
                                        metadata.getCommandAnnotation().parentCommand(), y -> new HashMap<>())
                                            .put(x, metadata));
                    }
                } catch (final ObjectMappingException e) {
                    e.printStackTrace();
                }

                if (!metadata.isRoot()) {
                    final String prefix =
                            metadataStringMap.get(metadata.getCommandAnnotation().parentCommand()).replace(".", " ");
                    for (final String x : metadata.getAtLevelAliases()) {
                        toRegister.computeIfAbsent(
                                metadata.getCommandAnnotation().parentCommand(),
                                y -> new HashMap<>()).put(prefix + " " + x, metadata);
                    }
                }
            }
        }

        // Now for mappings
        final Set<String> toRemove = new HashSet<>();
        for (final Map.Entry<String, String> entry : this.commandremap.entrySet()) {
            final CommentedConfigurationNode node = this.commandsConfConfigNode.getNode(entry.getKey()).getNode("enabled");
            if (node.isVirtual()) {
                node.setValue(true).setComment(serviceCollection.messageProvider().getMessageString("config.enabled"));
            } else if (!node.getBoolean(true)) {
                // remove from mapping
                toRemove.add(entry.getKey());
            }
        }

        toRemove.forEach(this.commandremap::remove);
        // save();

        // use aliases to register commands.
        this.register(toRegister, commands, ICommandExecutor.class, null, serviceCollection, event);

        // Okay, now we've created our commands, time to update command conf with the modifiers.
        this.mergeModifierDefaults();
        this.save();

        // Now set the data.
        this.setupData();
    }

    private <T extends ICommandExecutor> void register(
            final Map<Class<? extends ICommandExecutor>, Map<String, CommandMetadata>> toStart,
            final Map<CommandMetadata, CommandControl> commands,
            final Class<T> keyToCheck,
            @Nullable final CommandControl parentControl,
            final INucleusServiceCollection collection,
            final RegisterCommandEvent<org.spongepowered.api.command.Command.Parameterized> event) {

        for (final Map.Entry<String, CommandMetadata> entry : toStart.get(keyToCheck).entrySet()) {
            final CommandControl control = commands.computeIfAbsent(entry.getValue(), mm -> this.construct(parentControl, mm, collection));
            this.controlToExecutorClass.putIfAbsent(entry.getValue().getExecutor(), control);
            final Class<? extends ICommandExecutor> currentKey = entry.getValue().getExecutor();
            final boolean hasKey = toStart.containsKey(currentKey);
            if (hasKey) {
                // register entries with this executor.
                this.register(toStart, commands, entry.getValue().getExecutor(), control, collection, event);
            }

            // actual parent
            if (parentControl == null || !entry.getKey().contains(" ")) {
                this.controlToAliases.computeIfAbsent(control, c -> new ArrayList<>()).add(entry.getKey());
            } else {
                final String key = entry.getKey().substring(entry.getKey().lastIndexOf(" ") + 1);
                parentControl.attach(key, control);
            }

        }

        // Now we register all root commands as necessary.
        if (parentControl == null) {
            for (final Map.Entry<CommandControl, List<String>> aliases : this.controlToAliases.entrySet()) {
                // Ensure that the first entry in the list is the one specified first
                final CommandControl control = aliases.getKey();
                final List<String> orderedAliases = new ArrayList<>();
                final List<String> aliasesToAdd = new ArrayList<>(aliases.getValue());
                for (final String a : control.getMetadata().getRootAliases()) {
                    if (aliases.getValue().contains(a)) {
                        orderedAliases.add(a);
                        aliasesToAdd.remove(a);
                    }
                }

                // Additions
                orderedAliases.addAll(aliasesToAdd);
                final String first = orderedAliases.get(0);
                final Collection<String> others = orderedAliases.size() > 1 ? orderedAliases.subList(1, orderedAliases.size()) :
                        Collections.emptyList();
                this.registeredAliases.addAll(event.register(
                        collection.pluginContainer(),
                        this.createCommand(aliases.getKey()),
                        first,
                        others.toArray(new String[0])
                ).getAllAliases());
            }

            this.registeredCommands.putAll(commands);
        }
    }

    private void mergeAliases() {
        final CommentedConfigurationNode toMerge = this.configurateHelper.createNode();
        this.commandMetadataMap.values().forEach(metadata -> {
            final CommentedConfigurationNode node = toMerge.getNode(metadata.getCommandKey());
            final String messageKey = metadata.getCommandAnnotation().commandDescriptionKey() + ".desc";
            if (this.messageProviderService.hasKey(messageKey)) {
                node.setComment(this.messageProviderService.getMessageString(messageKey));
            }
            node.getNode(ENABLED).setComment(this.messageProviderService.getMessageString("config.enabled")).setValue(true);
            final CommentedConfigurationNode al = node.getNode(ROOT_ALIASES);
            for (final String a : metadata.getRootAliases()) {
                al.getNode(a).setValue(!metadata.getDisabledByDefaultRootAliases().contains(a));
            }
            if (!al.isVirtual()) {
                al.setComment(this.messageProviderService.getMessageString("config.rootaliases"));
            }
        });

        this.commandsConfConfigNode.mergeValuesFrom(toMerge);
    }

    private void mergeModifierDefaults() {
        final CommentedConfigurationNode toMerge = this.configurateHelper.createNode();
        this.controlToAliases.keySet().forEach(control -> {
            final CommentedConfigurationNode node = toMerge.getNode(control.getCommandKey());
            if (!control.isModifierKeyRedirected()) { // if redirected, another command will deal with this.
                for (final Map.Entry<CommandModifier, ICommandModifier> modifier : control.getCommandModifiers().entrySet()) {
                    if (modifier.getKey().useFrom() == ICommandExecutor.class) {
                        modifier.getValue().getDefaultNode(node, this.messageProviderService);
                    }
                }
            }
        });

        this.commandsConfConfigNode.mergeValuesFrom(toMerge);
    }

    private void setupData() {
        final List<Action> postponeAction = new ArrayList<>();
        this.controlToAliases.keySet().forEach(control -> {
            final CommentedConfigurationNode node;
            if (control.isModifierKeyRedirected()) {
                node = this.commandsConfConfigNode.getNode(control.getMetadata().getCommandAnnotation().modifierOverride());
            } else {
                node = this.commandsConfConfigNode.getNode(control.getCommandKey());
            }
            for (final Map.Entry<CommandModifier, ICommandModifier> modifier : control.getCommandModifiers().entrySet()) {
                if (modifier.getKey().useFrom() != ICommandExecutor.class) {
                    final CommandControl useFromControl = this.controlToExecutorClass.get(modifier.getKey().useFrom());
                    postponeAction.add(() ->
                            modifier.getValue().setValueFromOther(useFromControl.getCommandModifiersConfig(), control.getCommandModifiersConfig()));
                }
                modifier.getValue().setDataFromNode(control.getCommandModifiersConfig(), node);
            }
        });

        postponeAction.forEach(Action::action);
    }

    private CommandControl construct(@Nullable final CommandControl parent, final CommandMetadata metadata, final INucleusServiceCollection serviceCollection) {
        final ICommandExecutor executor = serviceCollection.injector().getInstance(metadata.getExecutor());
        return new CommandControl(
                executor,
                parent,
                metadata,
                serviceCollection
        );
    }

    @Override public Optional<CommandControl> getControl(final Class<? extends ICommandExecutor> executorClass) {
        return Optional.ofNullable(this.controlToExecutorClass.get(executorClass));
    }

    @Override public Collection<CommandControl> getCommands() {
        return this.controlToAliases.keySet();
    }

    @Override public void registerInterceptors(final Collection<ICommandInterceptor> commandInterceptors) {
        for (final ICommandInterceptor interceptor : commandInterceptors) {
            this.registerInterceptor(interceptor);
        }
    }

    @Override public void registerInterceptor(final ICommandInterceptor impl) {
        if (impl instanceof IReloadableService.Reloadable) {
            this.reloadableService.registerReloadable((IReloadableService.Reloadable) impl);
        }
        this.interceptors.add(impl);
    }

    @Override public Collection<ICommandInterceptor> interceptors() {
        return ImmutableList.copyOf(this.interceptors);
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        // reload the file.
        this.load();
        this.setupData();
    }

    private void load() {
        try {
            this.commandsConfConfigNode = HoconConfigurationLoader
                    .builder()
                    .setPath(this.commandsFile)
                    .build()
                    .load();
        } catch (final IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            HoconConfigurationLoader
                    .builder()
                    .setPath(this.commandsFile)
                    .build()
                    .save(this.commandsConfConfigNode);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private org.spongepowered.api.command.Command.Parameterized createCommand(final CommandControl control) {
        // TODO: THIS MUST BE DONE BEFORE EVEN THINKING ABOUT TRYING IT
        return null;
    }
}
