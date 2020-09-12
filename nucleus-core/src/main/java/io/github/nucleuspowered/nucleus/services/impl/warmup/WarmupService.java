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
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;

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

    @Override
    public void executeAfter(final Player target, final Duration duration, final WarmupTask runnable) {
        synchronized (this.lockingObject) {
            this.cancelInternal(target);

            // build the task
            final UUID playerTarget = target.getUniqueId();
            final Consumer<ScheduledTask> taskToSubmit = (ScheduledTask task) -> {
                this.tasks.remove(playerTarget);
                this.uuidToWarmup.remove(task.getUniqueId());

                if (Sponge.getServer().getPlayer(playerTarget).isPresent()) {
                    // Only run if the player is still on the server.
                    runnable.run();
                }
            };

            final Task t = Task.builder()
                    .delay(duration.toMillis(), TimeUnit.MILLISECONDS)
                    .name("Nucleus Warmup task: " + playerTarget.toString())
                    .execute(taskToSubmit)
                    .plugin(this.pluginContainer)
                    .build();
            final ScheduledTask scheduledTask = Sponge.getServer().getScheduler().submit(t);
            this.tasks.put(playerTarget, scheduledTask.getUniqueId());
            this.uuidToWarmup.put(scheduledTask.getUniqueId(), runnable);
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
            Sponge.getServer().getScheduler().getTaskById(taskUUID).ifPresent(ScheduledTask::cancel);
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
                if (Sponge.getServer().getScheduler().getTaskById(taskUUID).isPresent()) {
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
