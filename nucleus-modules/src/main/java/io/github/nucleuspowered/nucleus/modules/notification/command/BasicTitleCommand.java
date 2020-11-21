/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.command;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.notification.NotificationPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

@Command(
        aliases = "basictitle",
        basePermission = NotificationPermissions.BASE_BASICTITLE,
        commandDescriptionKey = "basictitle",
        associatedPermissions = NotificationPermissions.BASICTITLE_MULTI
)
public final class BasicTitleCommand extends TitleBase {

    @Inject
    public BasicTitleCommand() {
        super(NotificationPermissions.BASICTITLE_MULTI, "Title");
    }

    @Override
    protected Title createTitle(final Component text, final Title.Times times) {
        return Title.title(text, Component.empty(), times);
    }
}
