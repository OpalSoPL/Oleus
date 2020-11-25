/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command;

import io.github.nucleuspowered.nucleus.core.scaffold.command.impl.CommandResultImpl;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;

import java.util.Optional;

public interface ICommandResult {

    static ICommandResult success() {
        return CommandResultImpl.SUCCESS;
    }

    static ICommandResult fail() {
        return CommandResultImpl.FAILURE;
    }

    static ICommandResult willContinueLater() {
        return CommandResultImpl.WILL_CONTINUE;
    }

    boolean isSuccess();

    boolean isWillContinue();

    Optional<Component> getErrorMessage(ICommandContext source);

    CommandResult getResult(ICommandContext source);

}
