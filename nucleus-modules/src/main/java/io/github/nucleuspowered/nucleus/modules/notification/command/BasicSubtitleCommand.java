/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.command;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.notification.NotificationPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

@Command(
        aliases = "basicsubtitle",
        basePermission = NotificationPermissions.BASE_BASICSUBTITLE,
        commandDescriptionKey = "basicsubtitle",
        associatedPermissions = NotificationPermissions.BASICSUBTITLE_MULTI
)
public class BasicSubtitleCommand extends TitleBase {

    @Inject
    public BasicSubtitleCommand() {
        super(NotificationPermissions.BASICSUBTITLE_MULTI, "Subtitle");
    }

    @Override
    protected Title createTitle(final Component text, final Title.Times times) {
        return Title.title(Component.empty(), text, times);
    }
}
