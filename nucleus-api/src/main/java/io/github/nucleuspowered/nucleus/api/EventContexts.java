/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContextKey;

/**
 * Contexts that may appear in the {@link Cause} of some events.
 */
public final class EventContexts {

    private EventContexts() {}

    /**
     * A context that indicates whether the Nucleus chat events will perform its own formatting.
     *
     * <p>
     *     For the ID, see {@link Identifiers#SHOULD_FORMAT_CHANNEL}
     * </p>
     */
    public static final EventContextKey<Boolean> SHOULD_FORMAT_CHANNEL =
            EventContextKey.builder()
                    .type(Boolean.class)
                    .key(Identifiers.SHOULD_FORMAT_CHANNEL)
                    .build();

    /**
     * A context that indicates whether a teleport is a jailing action.
     *
     * <p>
     *     For the ID, see {@link Identifiers#IS_JAILING_ACTION}
     * </p>
     */
    public static final EventContextKey<Boolean> IS_JAILING_ACTION =
            EventContextKey.builder()
                    .type(Boolean.class)
                    .key(Identifiers.IS_JAILING_ACTION)
                    .build();

    /**
     * A context that indicates whether teleports should ignore the fact someone is jailed.
     *
     * <p>
     *     For the ID, see {@link Identifiers#BYPASS_JAILING_RESTRICTION }
     * </p>
     */
    public static final EventContextKey<Boolean> BYPASS_JAILING_RESTRICTION =
            EventContextKey.builder()
                    .type(Boolean.class)
                    .key(Identifiers.BYPASS_JAILING_RESTRICTION)
                    .build();

    public final static class Identifiers {

        private Identifiers() {}

        /**
         * ID for {@link EventContexts#SHOULD_FORMAT_CHANNEL}
         */
        public static final ResourceKey SHOULD_FORMAT_CHANNEL = ResourceKey.resolve("nucleus:should_format_channel");

        /**
         * ID for {@link EventContexts#IS_JAILING_ACTION}
         */
        public static final ResourceKey IS_JAILING_ACTION = ResourceKey.resolve("nucleus:is_jailing_action");

        /**
         * ID for {@link EventContexts#BYPASS_JAILING_RESTRICTION}
         */
        public static final ResourceKey BYPASS_JAILING_RESTRICTION = ResourceKey.resolve("nucleus:bypass_jailing_restriction");

    }

}
