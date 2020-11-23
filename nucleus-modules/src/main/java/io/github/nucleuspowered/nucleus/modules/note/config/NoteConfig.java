/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class NoteConfig {

    @Setting(value = "show-login")
    @LocalisedComment("config.note.showonlogin")
    private boolean showOnLogin = true;

    public boolean isShowOnLogin() {
        return this.showOnLogin;
    }
}
