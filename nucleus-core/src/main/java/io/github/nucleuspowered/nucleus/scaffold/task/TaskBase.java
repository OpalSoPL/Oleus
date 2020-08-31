/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.task;

import io.github.nucleuspowered.nucleus.scaffold.EntryPoint;
import org.spongepowered.api.scheduler.Task;
import java.time.Duration;
import java.util.function.Consumer;

@EntryPoint
public interface TaskBase extends Runnable {

    Duration interval();

}
