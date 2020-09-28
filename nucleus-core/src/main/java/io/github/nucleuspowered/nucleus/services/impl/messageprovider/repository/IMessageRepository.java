/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository;

import net.kyori.adventure.text.Component;

public interface IMessageRepository {

    /**
     * Will only invalidate if it's outside the jar
     */
    default void invalidateIfNecessary() {}

    boolean hasEntry(String key);

    Component getText(String key);

    Component getText(String key, Object[] args);

    String getString(String key);

    String getString(String key, Object[] args);

}
