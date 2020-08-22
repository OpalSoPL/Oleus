/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.warmup;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.services.interfaces.IWarmupService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Singleton
public class WarmupService implements IWarmupService {

    private final Object lockingObject = new Object();

    private final PluginContainer pluginContainer;

    // player to task
    private final BiMap<UUID, UUID> tasks = HashBiMap.create();

    // task to warmup
    private final BiMap<UUID, WarmupTask> uuidToWarmup = HashBiMap.create();

    @Inject
    public WarmupService(final PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
    }

    @Override public void executeAfter(final Player target, final Duration duration, final WarmupTask runnable) {
        this.execute(target, duration, runnable, false);
    }

    @Override public void executeAfterAsync(final Player target, final Duration duration, final WarmupTask runnable) {
        this.execute(target, duration, runnable, true);
    }

    private void execute(final Player target, final Duration duration, final WarmupTask runnable, final boolean async) {
        synchronized (this.lockingObject) {
            this.cancelInternal(target);

            // build the task
            final UUID playerTarget = target.getUniqueId();
            final Consumer<Task> taskToSubmit = task -> {
                if (Sponge.getServer().getPlayer(playerTarget).isPresent()) {
                    // Only run if the player is still on the server.
                    runnable.run();
                }

                this.tasks.remove(playerTarget);
                this.uuidToWarmup.remove(task.getUniqueId());
            };

            final Task.Builder builder = Task.builder()
                    .execute(taskToSubmit)
                    .delay(duration.toMillis(), TimeUnit.MILLISECONDS)
                    .name("Nucleus Warmup task: " + playerTarget.toString());
            if (async) {
                builder.async();
            }
            final Task t = builder.submit(this.pluginContainer);
            this.tasks.put(playerTarget, t.getUniqueId());
            this.uuidToWarmup.put(t.getUniqueId(), runnable);
        }
    }

    @Override public boolean cancel(final Player player) {
        synchronized (this.lockingObject) {
            return this.cancelInternal(player);
        }
    }

    private boolean cancelInternal(final Player player) {
        final UUID taskUUID = this.tasks.get(player.getUniqueId());
        if (taskUUID != null) {
            Sponge.getScheduler().getTaskById(taskUUID).ifPresent(Task::cancel);
            final WarmupTask task = this.uuidToWarmup.get(taskUUID);
            if (task != null) {
                // if we get here, it was never cancelled.
                task.onCancel();
            }

            this.uuidToWarmup.remove(taskUUID);
        }
        this.tasks.remove(player.getUniqueId());
        return taskUUID != null;
    }

    @Override public boolean awaitingExecution(final Player player) {
        synchronized (this.lockingObject) {
            final UUID taskUUID = this.tasks.get(player.getUniqueId());
            if (taskUUID != null) {
                if (Sponge.getScheduler().getTaskById(taskUUID).isPresent()) {
                    // remove entries
                    return true;
                } else {
                    this.uuidToWarmup.remove(taskUUID);
                }
            }
            this.tasks.remove(player.getUniqueId());
            return false;
        }
    }
}
