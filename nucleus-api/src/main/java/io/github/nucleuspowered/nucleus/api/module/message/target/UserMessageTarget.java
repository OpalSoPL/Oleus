package io.github.nucleuspowered.nucleus.api.module.message.target;

import java.util.UUID;

/**
 * A message target that targets a user.
 */
public interface UserMessageTarget extends MessageTarget {

    UUID getUserUUID();

}
