package io.github.nucleuspowered.nucleus.api.module.message.target;

import net.kyori.adventure.text.Component;

/**
 * A message target that can be messaged via private message.
 */
public interface CustomMessageTarget extends MessageTarget {

    /**
     * The identifier that players must use to message this target.
     *
     * <p>
     *     The identifier must be prefixed with # to indicate that a custom target has been set.
     * </p>
     *
     * @return The identifier.
     */
    String getIdentifier();

    /**
     * Gets the name that is displayed when this target is messaged/is messaging.
     *
     * @return The {@link Component}
     */
    Component getDisplayName();



}
