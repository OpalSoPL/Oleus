/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;

public interface ICommandInterceptor {

    void onPreCommand(Class<? extends ICommandExecutor> commandClass,
            CommandControl commandControl,
            ICommandContext context);

    void onPostCommand(Class<? extends ICommandExecutor> commandClass,
            CommandControl commandControl,
            ICommandContext context,
            ICommandResult result);
}
