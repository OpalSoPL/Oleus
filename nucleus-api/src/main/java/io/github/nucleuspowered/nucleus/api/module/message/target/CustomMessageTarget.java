/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message.target;

/**
 * A message target that can be messaged via private message.
 *
 * <p>This interface is designed to be implemented by the target itself.</p>
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

}
