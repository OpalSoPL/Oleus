/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.storage.services;

import io.github.nucleuspowered.nucleus.util.ThrownSupplier;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.concurrent.CompletableFuture;

public final class ServicesUtil {

    public static <R> CompletableFuture<R> run(final ThrownSupplier<R, Exception> taskConsumer, final PluginContainer pluginContainer) {
        final CompletableFuture<R> future = new CompletableFuture<>();

        if (Sponge.isServerAvailable() && Sponge.getServer().onMainThread()) {
            Sponge.getAsyncScheduler().createExecutor(pluginContainer).submit(() -> runInternal(future, taskConsumer));
        } else {
            runInternal(future, taskConsumer);
        }

        return future;
    }

    private static <R> void runInternal(final CompletableFuture<R> future, final ThrownSupplier<R, Exception> taskConsumer) {
        try {
            future.complete(taskConsumer.get());
        } catch (final Exception e) {
            future.completeExceptionally(e);
        }
    }
}
