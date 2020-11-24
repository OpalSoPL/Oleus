/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.vanish.commands.ToggleVanishOnLoginCommand;
import io.github.nucleuspowered.nucleus.modules.vanish.commands.VanishCommand;
import io.github.nucleuspowered.nucleus.modules.vanish.config.VanishConfig;
import io.github.nucleuspowered.nucleus.modules.vanish.listener.VanishListener;
import io.github.nucleuspowered.nucleus.modules.vanish.services.VanishService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.util.Identifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class VanishModule implements IModule.Configurable<VanishConfig> {

    public static final String ID = "vanish";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(VanishService.class, new VanishService(serviceCollection), false);
        serviceCollection.placeholderService()
                .registerToken(
                        "vanished",
                        PlaceholderParser.builder()
                                .key(ResourceKey.of("nucleus", "vanished"))
                                .parser(p -> {
                                    if (p.getAssociatedObject()
                                            .filter(x -> x instanceof Identifiable)
                                            .map(x -> serviceCollection.getServiceUnchecked(VanishService.class).isVanished(((Identifiable) x).getUniqueId()))
                                            .orElse(false)) {
                                        return Component.text("[Vanished]", NamedTextColor.GRAY);
                                    }
                                    return Component.empty();
                                }).build()
                );
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                ToggleVanishOnLoginCommand.class,
                VanishCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(VanishPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(VanishListener.class);
    }

    @Override public Class<VanishConfig> getConfigClass() {
        return VanishConfig.class;
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(VanishKeys.VANISH_ON_LOGIN);
    }

}
