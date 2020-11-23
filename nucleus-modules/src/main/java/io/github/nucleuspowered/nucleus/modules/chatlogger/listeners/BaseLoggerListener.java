/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger.listeners;

import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;

import java.io.IOException;

import com.google.inject.Inject;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;

public class BaseLoggerListener extends AbstractLoggerListener {

    @Inject
    BaseLoggerListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection);
    }

    @Listener
    public void onShutdown(final StoppingEngineEvent<Server> event) {
        try {
            this.handler.onServerShutdown();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return this.getConfig(serviceCollection).isEnableLog();
    }
}
