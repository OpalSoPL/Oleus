/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.connectionmessages.config;

import io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ConnectionMessagesConfig {

    @Setting(value = "show-first-login-message")
    @LocalisedComment("config.connectionmessages.enablefirst")
    private boolean showFirstTimeMessage = true;

    @Setting(value = "first-login-message")
    @LocalisedComment("config.connectionmessages.firsttime")
    private String firstTimeMessage = "&dWelcome &f{{name}} &dto the server!";

    @Setting(value = "modify-login-message")
    @LocalisedComment("config.connectionmessages.enablelogin")
    private boolean modifyLoginMessage = false;

    @Setting(value = "modify-logout-message")
    @LocalisedComment("config.connectionmessages.enablelogout")
    private boolean modifyLogoutMessage = false;

    @Setting(value = "login-message")
    @LocalisedComment("config.connectionmessages.loginmessage")
    private String loginMessage = "&8[&a+&8] &f{{name}}";

    @Setting(value = "logout-message")
    @LocalisedComment("config.connectionmessages.logoutmessage")
    private String logoutMessage = "&8[&c-&8] &f{{name}}";

    @Setting(value = "disable-with-permission")
    @LocalisedComment("config.connectionmessages.disablepermission")
    private boolean disableWithPermission = false;

    @Setting(value = "display-name-change-if-changed")
    @LocalisedComment("config.connectionmessages.displayprior")
    private boolean displayPriorName = true;

    @Setting(value = "changed-name-message")
    @LocalisedComment("config.connectionmessages.displaypriormessage")
    private String priorNameMessage = "&f{{name}} &ewas previously known by a different name - they were known as &f{{previousname}}";

    @Setting(value = "force-show-all-connection-messages")
    @LocalisedComment("config.connectionmessages.showall")
    private boolean forceForAll = true;

    public boolean isShowFirstTimeMessage() {
        return this.showFirstTimeMessage;
    }

    public String getFirstTimeMessage() {
        return this.firstTimeMessage;
    }

    public boolean isModifyLoginMessage() {
        return this.modifyLoginMessage;
    }

    public boolean isModifyLogoutMessage() {
        return this.modifyLogoutMessage;
    }

    public String getLoginMessage() {
        return this.loginMessage;
    }

    public String getLogoutMessage() {
        return this.logoutMessage;
    }

    public boolean isDisableWithPermission() {
        return this.disableWithPermission;
    }

    public boolean isDisplayPriorName() {
        return this.displayPriorName;
    }

    public String getPriorNameMessage() {
        return this.priorNameMessage;
    }

    public boolean isForceForAll() {
        return this.forceForAll;
    }
}
