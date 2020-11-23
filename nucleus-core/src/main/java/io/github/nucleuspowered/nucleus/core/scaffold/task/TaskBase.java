/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.task;

import io.github.nucleuspowered.nucleus.core.scaffold.EntryPoint;

import java.time.Duration;

@EntryPoint
public interface TaskBase extends Runnable {

    Duration interval();

}
