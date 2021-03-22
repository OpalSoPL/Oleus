/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nameban.services;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.nameban.NucleusNameBanService;
import io.github.nucleuspowered.nucleus.api.module.nameban.exception.NameBanException;
import io.github.nucleuspowered.nucleus.modules.nameban.events.NameBanEvent;
import io.github.nucleuspowered.nucleus.core.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.core.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IConfigurateHelper;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@APIService(NucleusNameBanService.class)
public class NameBanHandler implements NucleusNameBanService, ServiceBase, IReloadableService.DataLocationReloadable {

    private final Map<String, String> entries = new HashMap<>();
    private final Supplier<Path> dataPath;
    private final IConfigurateHelper configurateOptions;
    private final PluginContainer pluginContainer;
    private Path currentPath;

    @Inject
    public NameBanHandler(final INucleusServiceCollection serviceCollection) {
        this.pluginContainer = serviceCollection.pluginContainer();
        this.dataPath = serviceCollection.dataDir();
        this.configurateOptions = serviceCollection.configurateHelper();
    }

    @Override
    public void addName(final String name, final String reason) throws NameBanException {
        if (Util.USERNAME_REGEX_PATTERN.matcher(name).matches()) {
            this.entries.put(name.toLowerCase(), reason);
            Sponge.eventManager().post(new NameBanEvent.Banned(name, reason, Sponge.server().causeStackManager().getCurrentCause()));
            Sponge.server().onlinePlayers().stream().filter(x -> x.getName().equalsIgnoreCase(name)).findFirst()
                    .ifPresent(x -> x.kick(LegacyComponentSerializer.legacyAmpersand().deserialize(reason)));
            Sponge.server().getScheduler().submit(Task.builder().execute(this::save).plugin(this.pluginContainer).build());
        } else {
            throw new NameBanException(
                    Component.text("That is not a valid username."), NameBanException.Reason.DISALLOWED_NAME);
        }
    }

    @Override public Optional<String> getReasonForBan(final String name) {
        Objects.requireNonNull(name);
        return Optional.ofNullable(this.entries.get(name.toLowerCase()));
    }

    @Override
    public void removeName(final String name) throws NameBanException {
        if (Util.USERNAME_REGEX_PATTERN.matcher(name).matches()) {
            final Optional<String> reason = this.getReasonForBan(name);
            if (reason.isPresent() && this.entries.remove(name.toLowerCase()) != null) {
                Sponge.eventManager().post(new NameBanEvent.Unbanned(name, reason.get(), Sponge.server().causeStackManager().getCurrentCause()));
            } else {
                throw new NameBanException(Component.text("Entry does not exist."), NameBanException.Reason.DOES_NOT_EXIST);
            }
        } else {
            throw new NameBanException(Component.text("That is not a valid username."), NameBanException.Reason.DISALLOWED_NAME);
        }
    }

    public void load() {
        try {
            final ConfigurationNode node = this.createLoader().load();
            // Lowercase the keys.
            this.entries.clear();
            node.childrenMap()
                    .forEach((k, v) -> {
                        final String lower = k.toString().toLowerCase();
                        if (!k.equals(lower)) {
                            this.entries.put(lower, v.getString());
                        }
                    });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            final ConfigurationNode node = CommentedConfigurationNode.root(this.configurateOptions.setOptions(ConfigurationOptions.defaults()));
            node.set(new TypeToken<Map<String, String>>() {}, this.entries);
            this.createLoader().save(node);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void onDataFileLocationChange(final INucleusServiceCollection serviceCollection) {
        // The path changes
        this.currentPath = this.dataPath.get().resolve("namebans.conf");
        this.load();
    }

    private GsonConfigurationLoader createLoader() {
        return GsonConfigurationLoader.builder()
                .path(this.currentPath)
                .defaultOptions(this.configurateOptions.setOptions(ConfigurationOptions.defaults()))
                .build();
    }
}
