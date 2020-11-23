/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.services.impl.scheduler.SchedulerService;

import java.util.concurrent.CompletableFuture;

@ImplementedBy(SchedulerService.class)
public interface ISchedulerService {

    CompletableFuture<Void> runOnMainThread(Runnable runnable);

}
