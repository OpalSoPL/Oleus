/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.core;

import org.spongepowered.api.entity.living.player.Player;

import java.time.Duration;

/**
 * Manages the warmups.
 */
public interface NucleusWarmupManagerService {

    /**
     * Executes a task after the specified time, if the target does
     * not move or run a command.
     *
     * @param target The {@link Player} to execute the task on.
     * @param duration The {@link Duration} to wait.
     * @param runnable The {@link WarmupTask} to execute.
     */
    void executeAfter(Player target, Duration duration, WarmupTask runnable);

    /**
     * Cancels a task by {@link Player}
     *
     * @param player The player that this task is attached to.
     * @return true if cancelled, false otherwise
     */
    boolean cancel(Player player);

    /**
     * Whether there is a task awaiting execution.
     *
     * @param player The player
     * @return true if so
     */
    boolean awaitingExecution(Player player);

    /**
     * Represents a task that will execute after some time.
     */
    interface WarmupTask extends Runnable {

        /**
         * Runs if the warmup is cancelled.
         */
        default void onCancel() {}

    }

}
