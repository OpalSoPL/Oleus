/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.task;

import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

/**
 * Represents a task that has actions to perform when it's cancelled.
 */
public interface CancellableTask extends Consumer<ScheduledTask> {

    /**
     * The actions to perform upon cancellation.
     */
    void onCancel();
}
