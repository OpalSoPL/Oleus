/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.config;

import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.core.services.interfaces.INucleusTextTemplateFactory;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MessageConfig {

    private static final String MESSAGE_SENDER_DEFAULT = "&7[me -> {{toDisplay}}&7]: &r";
    private static final String MESSAGE_RECEIVER_DEFAULT = "&7[{{fromDisplay}}&7 -> me]: &r";
    private static final String MESSAGE_SOCIAL_SPY_DEFAULT = "&7[SocialSpy] [{{fromDisplay}}&7 -> {{toDisplay}}&7]: &r";
    private static final String HELP_OP_DEFAULT = "&7HelpOp: {{name}} &7> &r";

    @Setting(value = "can-message-self")
    @LocalisedComment("config.message.canmessageself")
    private boolean canMessageSelf = false;

    @Setting(value = "helpop-prefix")
    @LocalisedComment("config.message.helpop.prefix")
    private String helpOpPrefix = MessageConfig.HELP_OP_DEFAULT;

    @Setting(value = "msg-receiver-prefix")
    @LocalisedComment("config.message.receiver.prefix")
    private String messageReceiverPrefix = MessageConfig.MESSAGE_RECEIVER_DEFAULT;

    @Setting(value = "msg-sender-prefix")
    @LocalisedComment("config.message.sender.prefix")
    private String messageSenderPrefix = MessageConfig.MESSAGE_SENDER_DEFAULT;

    @Setting(value = "socialspy")
    private SocialSpy socialSpy = new SocialSpy();

    public boolean isCanMessageSelf() {
        return this.canMessageSelf;
    }

    public NucleusTextTemplateImpl getHelpOpPrefix(final INucleusTextTemplateFactory textTemplateFactory) {
        if (this.helpOpPrefix == null) {
            // set default
            return textTemplateFactory.createFromAmpersandString(HELP_OP_DEFAULT);
        }

        return textTemplateFactory.createFromAmpersandString(this.helpOpPrefix);
    }

    public NucleusTextTemplateImpl getMessageReceiverPrefix(final INucleusTextTemplateFactory textTemplateFactory) {
        if (this.messageReceiverPrefix == null) {
            // set default
            return textTemplateFactory.createFromAmpersandString(MESSAGE_RECEIVER_DEFAULT);
        }

        return textTemplateFactory.createFromAmpersandString(this.messageReceiverPrefix);
    }

    public NucleusTextTemplateImpl getMessageSenderPrefix(final INucleusTextTemplateFactory textTemplateFactory) {
        if (this.messageSenderPrefix == null) {
            // set default
            return textTemplateFactory.createFromAmpersandString(MESSAGE_SENDER_DEFAULT);
        }

        return textTemplateFactory.createFromAmpersandString(this.messageSenderPrefix);
    }

    public NucleusTextTemplateImpl getMessageSocialSpyPrefix(final INucleusTextTemplateFactory textTemplateFactory) {
        if (this.socialSpy.messageSocialSpyPrefix == null) {
            // set default
            return textTemplateFactory.createFromAmpersandString(MESSAGE_SOCIAL_SPY_DEFAULT);
        }

        return textTemplateFactory.createFromAmpersandString(this.socialSpy.messageSocialSpyPrefix);
    }

    public boolean isSocialSpyAllowForced() {
        return this.socialSpy.allowForced;
    }

    public boolean isSocialSpyLevels() {
        return this.socialSpy.socialSpyLevels;
    }

    public boolean isSocialSpySameLevel() {
        return this.socialSpy.socialSpySameLevel;
    }

    public int getCustomTargetLevel() {
        return this.socialSpy.level.customTargets;
    }

    public int getServerLevel() {
        return this.socialSpy.level.server;
    }

    public boolean isShowMessagesInSocialSpyWhileMuted() {
        return this.socialSpy.showMessagesInSocialSpyWhileMuted;
    }

    public String getMutedTag() {
        return this.socialSpy.mutedTag;
    }

    public String getBlockedTag() {
        return this.socialSpy.blocked;
    }

    public Targets spyOn() {
        return this.socialSpy.targets;
    }

    @ConfigSerializable
    public static class SocialSpy {
        @Setting(value = "msg-prefix")
        @LocalisedComment("config.message.socialspy.prefix")
        private String messageSocialSpyPrefix = MessageConfig.MESSAGE_SOCIAL_SPY_DEFAULT;

        @Setting(value = "allow-forced")
        @LocalisedComment("config.message.socialspy.force")
        private boolean allowForced = false;

        @Setting(value = "use-levels")
        @LocalisedComment("config.message.socialspy.levels")
        private boolean socialSpyLevels = false;

        @Setting(value = "same-levels-can-see-each-other")
        @LocalisedComment("config.message.socialspy.samelevel")
        private boolean socialSpySameLevel = true;

        @Setting(value = "levels")
        @LocalisedComment("config.message.socialspy.serverlevels")
        private Levels level = new Levels();

        @Setting(value = "show-cancelled-messages")
        @LocalisedComment("config.message.socialspy.mutedshow")
        private boolean showMessagesInSocialSpyWhileMuted = false;

        @Setting(value = "cancelled-messages-tag")
        @LocalisedComment("config.message.socialspy.mutedtag")
        private String mutedTag = "&c[cancelled] ";

        @Setting(value = "msgtoggle-blocked-messages-tag")
        @LocalisedComment("config.message.socialspy.msgtoggle")
        private String blocked = "&c[blocked] ";

        @Setting(value = "senders-to-spy-on")
        @LocalisedComment("config.message.socialspy.spyon")
        private Targets targets = new Targets();
    }

    @ConfigSerializable
    public static class Levels {

        @Setting(value = "server")
        @LocalisedComment("config.message.socialspy.serverlevel")
        private int server = Integer.MAX_VALUE;

        @Setting(value = "custom-targets")
        @LocalisedComment("config.message.socialspy.customlevel")
        private int customTargets = Integer.MAX_VALUE;
    }

    @ConfigSerializable
    public static class Targets {

        @Setting
        private boolean player = true;

        @Setting
        private boolean server = true;

        @Setting(value = "custom-target")
        private boolean custom = true;

        public boolean isPlayer() {
            return this.player;
        }

        public boolean isServer() {
            return this.server;
        }

        public boolean isCustom() {
            return this.custom;
        }
    }
}
