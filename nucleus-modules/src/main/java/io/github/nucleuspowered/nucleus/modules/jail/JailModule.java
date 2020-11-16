/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.data.JailData;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlaceholderService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderParser;

import java.util.Collection;
import java.util.Optional;

public class JailModule implements IModule.Configurable<JailConfig> { //extends ConfigurableModule<JailConfig, JailConfigAdapter> {

    public static final String ID = "jail";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        final JailHandler handler = new JailHandler(serviceCollection);
        serviceCollection.registerService(JailHandler.class, handler, false);
        serviceCollection.userCacheService().setJailProcessor(x -> x.get(JailKeys.JAIL_DATA).map(JailData::getJailName).orElse(null));
        final IPlaceholderService placeholderService = serviceCollection.placeholderService();
        placeholderService.registerToken("jailed", PlaceholderParser.builder()
                .key(ResourceKey.of(serviceCollection.pluginContainer(), "jailed"))
                .parser(p -> {
                    if (p.getAssociatedObject().filter(x -> x instanceof User)
                            .map(x -> serviceCollection.getServiceUnchecked(JailHandler.class).isPlayerJailed((User) x))
                            .orElse(false)) {
                        return Component.text("[Jailed]", NamedTextColor.GRAY);
                    }
                    return Component.empty();
                }).build());
        placeholderService.registerToken("jail", PlaceholderParser.builder()
                .key(ResourceKey.of(serviceCollection.pluginContainer(), "jail"))
                .parser(placeholder -> {
                    if (placeholder.getAssociatedObject().filter(x -> x instanceof Player).isPresent()) {
                        return serviceCollection.getServiceUnchecked(JailHandler.class)
                                .getPlayerJailData(((ServerPlayer) placeholder.getAssociatedObject().get()).getUniqueId())
                                .<Component>map(x -> Component.text(x.getJailName()))
                                .orElseGet(Component::empty);
                    }

                    return Component.empty();
                }).build());
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(JailPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }


    @Override public Class<JailConfig> getConfigClass() {
        return null;
    }
}
