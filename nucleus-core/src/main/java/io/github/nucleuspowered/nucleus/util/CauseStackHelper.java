/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

public class CauseStackHelper {

    private CauseStackHelper() {}

    public static Cause createCause(final Object o) {
        if (Sponge.getServer().isMainThread()) {
            try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                frame.pushCause(o);
                return Sponge.getCauseStackManager().getCurrentCause();
            }
        }

        return Cause.builder().append(o).build(EventContext.empty());
    }

}
