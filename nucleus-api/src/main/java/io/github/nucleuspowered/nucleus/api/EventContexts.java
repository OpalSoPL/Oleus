/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api;

import io.github.nucleuspowered.nucleus.api.module.spawn.event.NucleusSendToSpawnEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContextKey;
import org.spongepowered.api.event.EventContextKeys;

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
                    .id(Identifiers.SHOULD_FORMAT_CHANNEL)
                    .name("Nucleus - Context to indicate whether a chat message should be formatted.")
                    .build();

    /**
     * A context that indicates the source of the Nucleus redirectable spawn events.
     *
     * <p>
     *     For the ID, see {@link Identifiers#SPAWN_EVENT_TYPE}
     * </p>
     */
    public static final EventContextKey<NucleusSendToSpawnEvent.Type> SPAWN_EVENT_TYPE =
            EventContextKey.builder()
                    .type(NucleusSendToSpawnEvent.Type.class)
                    .id(Identifiers.SPAWN_EVENT_TYPE)
                    .name("SPAWN_EVENT_TYPE")
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
                    .id("nucleus:is_jailing_action")
                    .name("Nucleus - Context to indicate whether a teleport is a jailing teleport.")
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
                    .id("nucleus:bypass_jailing_restriction")
                    .name("Nucleus - Context to indicate whether the Nucleus system should ignore jailed status when teleporting a player.")
                    .build();

    public final static class Identifiers {

        private Identifiers() {}

        /**
         * ID for {@link EventContexts#SHOULD_FORMAT_CHANNEL}
         */
        public static final String SHOULD_FORMAT_CHANNEL = "nucleus:should_format_channel";

        /**
         * ID for {@link EventContexts#SPAWN_EVENT_TYPE}
         */
        public static final String SPAWN_EVENT_TYPE = "nucleus:spawn_event_type";

        /**
         * ID for {@link EventContexts#IS_JAILING_ACTION}
         */
        public static final String IS_JAILING_ACTION = "nucleus:is_jailing_action";

        /**
         * ID for {@link EventContexts#BYPASS_JAILING_RESTRICTION}
         */
        public static final String BYPASS_JAILING_RESTRICTION = "nucleus:bypass_jailing_restriction";

    }

}
