/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.vanish.config.VanishConfig;
import io.github.nucleuspowered.nucleus.modules.vanish.services.VanishService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.util.Identifiable;

import java.util.Collection;
import java.util.Optional;

public final class VanishModule implements IModule.Configurable<VanishConfig> {

    public static final String ID = "vanish";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
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
        return null;
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }

    @Override public Collection<Class<? extends TaskBase>> getTasks() {
        return null;
    }

    @Override public Class<VanishConfig> getConfigClass() {
        return null;
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(
                new PreferenceKeyImpl.BooleanKey(
                        NucleusKeysProvider.VANISH_ON_LOGIN_KEY,
                        false,
                        VanishPermissions.VANISH_ONLOGIN,
                        "userpref.vanishonlogin"
                )
        );
    }

}
