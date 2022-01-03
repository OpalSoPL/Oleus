/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rules;

import io.github.nucleuspowered.nucleus.core.io.TextFileController;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.rules.commands.RulesCommand;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.resource.ResourcePath;
import org.spongepowered.api.resource.pack.Pack;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class RulesModule implements IModule.Configurable<RulesConfig> {

    public static final String ID = "rules";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        final Pack pack = Sponge.server().packRepository().pack(serviceCollection.pluginContainer());
        serviceCollection.textFileControllerCollection()
                .register(ID,
                        new TextFileController(
                                serviceCollection.pluginContainer(),
                                serviceCollection.textTemplateFactory(),
                                ResourcePath.of(pack.id(), "rules.txt"),
                                serviceCollection.configDir().resolve("rules.txt")
                        ));
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singleton(RulesCommand.class);
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(RulesPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override public Class<RulesConfig> getConfigClass() {
        return RulesConfig.class;
    }
}
