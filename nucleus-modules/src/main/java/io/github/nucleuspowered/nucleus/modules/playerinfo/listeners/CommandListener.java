/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.listeners;

import io.github.nucleuspowered.nucleus.modules.playerinfo.config.PlayerInfoConfig;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.filter.Getter;

import com.google.inject.Inject;
import org.spongepowered.plugin.PluginContainer;

public class CommandListener implements ListenerBase.Conditional {

    private final IMessageProviderService messageProviderService;
    private final PluginContainer pluginContainer;
    private boolean messageShown = false;

    @Inject
    public CommandListener(final INucleusServiceCollection serviceCollection) {
        this.messageProviderService = serviceCollection.messageProvider();
        this.pluginContainer = serviceCollection.pluginContainer();
    }

    @Listener
    public void onCommandPreProcess(final ExecuteCommandEvent.Pre event, @Getter("getCommand") final String command) {
        final Cause cause = event.getCause();
        if (cause.root() == Sponge.getSystemSubject() || cause.root() == Sponge.getServer()) {
            if (command.equalsIgnoreCase("list")) {
                event.setCommand("minecraft:list");
                if (!this.messageShown) {
                    this.messageShown = true;
                    Sponge.getServer().getScheduler().createExecutor(this.pluginContainer).submit(() ->
                            this.messageProviderService.sendMessageTo(Sponge.getSystemSubject(), "list.listener.multicraftcompat"));
                }
            }
        }
    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(PlayerInfoConfig.class).getList().isPanelCompatibility();
    }

}
