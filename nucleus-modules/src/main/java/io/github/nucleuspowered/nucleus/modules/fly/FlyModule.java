/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fly;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.fly.commands.FlyCommand;
import io.github.nucleuspowered.nucleus.modules.fly.config.FlyConfig;
import io.github.nucleuspowered.nucleus.modules.fly.listeners.FlyListener;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.placeholder.PlaceholderParser;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class FlyModule implements IModule.Configurable<FlyConfig> {

    public static final String ID = "fly";

    private static final Component FLYING_TOKEN = Component.text("[Flying]", NamedTextColor.GRAY);

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.placeholderService().registerToken(
                "flying",
                PlaceholderParser.builder()
                        .parser(p -> {
                            if (p.associatedObject().filter(x -> x instanceof Player).flatMap(x -> ((Player) x).get(Keys.IS_FLYING))
                                    .orElse(false)) {
                                return FlyModule.FLYING_TOKEN;
                            }
                            return Component.empty();
                        })
                        .build());
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singleton(FlyCommand.class);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(FlyPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(FlyListener.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<FlyConfig> getConfigClass() {
        return FlyConfig.class;
    }

}
