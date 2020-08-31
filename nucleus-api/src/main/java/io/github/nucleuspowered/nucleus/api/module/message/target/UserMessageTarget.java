/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.message.target;

import java.util.UUID;

/**
 * A message target that targets a user.
 */
public interface UserMessageTarget extends MessageTarget {

    UUID getUserUUID();

}
