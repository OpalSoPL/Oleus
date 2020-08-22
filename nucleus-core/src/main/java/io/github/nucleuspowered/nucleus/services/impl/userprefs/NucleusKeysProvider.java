/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.userprefs;

import com.google.common.collect.ImmutableSet;
import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyModule;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyPermissions;
import io.github.nucleuspowered.nucleus.modules.core.CoreModule;
import io.github.nucleuspowered.nucleus.modules.core.CorePermissions;
import io.github.nucleuspowered.nucleus.modules.message.MessageModule;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolModule;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolPermissions;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatModule;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatPermissions;
import io.github.nucleuspowered.nucleus.modules.teleport.TeleportModule;
import io.github.nucleuspowered.nucleus.modules.teleport.TeleportPermissions;
import io.github.nucleuspowered.nucleus.modules.vanish.VanishModule;
import io.github.nucleuspowered.nucleus.modules.vanish.VanishPermissions;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IModuleDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class NucleusKeysProvider implements NucleusUserPreferenceService.Keys {

    private final IModuleDataProvider moduleDataProvider;
    public NucleusKeysProvider(final INucleusServiceCollection serviceCollection) {
        this.moduleDataProvider = serviceCollection.moduleDataProvider();
    }

    public final static String COMMAND_SPY_KEY = "nucleus:command-spy";
    public final static String MESSAGE_TOGGLE_KEY = "nucleus:message-receiving-enabled";
    public static final String PLAYER_LOCALE_KEY = "nucleus:player-locale";
    public final static String POWERTOOL_ENABLED_KEY = "nucleus:powertool-toggle";
    public final static String SOCIAL_SPY_KEY = "nucleus:social-spy";
    public static final String TELEPORT_TARGETABLE_KEY = "nucleus:teleport-targetable";
    public static final String VANISH_ON_LOGIN_KEY = "nucleus:vanish-on-login";
    public static final String VIEW_STAFF_CHAT_KEY = "nucleus:view-staff-chat";

    public static final PreferenceKeyImpl<Boolean> COMMAND_SPY = new PreferenceKeyImpl.BooleanKey(
            COMMAND_SPY_KEY,
            true,
            CommandSpyPermissions.BASE_COMMANDSPY,
            "userpref.commandspy",
            CommandSpyModule.ID
    );
    public static final PreferenceKeyImpl<Boolean> RECEIVING_MESSAGES = new PreferenceKeyImpl.BooleanKey(
            MESSAGE_TOGGLE_KEY,
            true,
            MessagePermissions.MSGTOGGLE_BYPASS,
            "userpref.messagetoggle",
            MessageModule.ID
    );
    public static final PreferenceKeyImpl<Boolean> POWERTOOL_ENABLED = new PreferenceKeyImpl.BooleanKey(
            POWERTOOL_ENABLED_KEY,
            true,
            PowertoolPermissions.BASE_POWERTOOL,
            "userpref.powertooltoggle",
            PowertoolModule.ID
    );
    public static final PreferenceKeyImpl<Boolean> TELEPORT_TARGETABLE = new PreferenceKeyImpl.BooleanKey(
            TELEPORT_TARGETABLE_KEY,
            true,
            TeleportPermissions.BASE_TPTOGGLE,
            "userpref.teleporttarget",
            TeleportModule.ID
    );
    public static final PreferenceKeyImpl<Boolean> VANISH_ON_LOGIN = new PreferenceKeyImpl.BooleanKey(
            VANISH_ON_LOGIN_KEY,
            false,
            VanishPermissions.VANISH_ONLOGIN,
            "userpref.vanishonlogin",
            VanishModule.ID
    );
    public static final PreferenceKeyImpl<Boolean> VIEW_STAFF_CHAT = new PreferenceKeyImpl.BooleanKey(
            VIEW_STAFF_CHAT_KEY,
            true,
            StaffChatPermissions.BASE_STAFFCHAT,
            "userpref.viewstaffchat",
            StaffChatModule.ID
    );
    public static final PreferenceKeyImpl<Boolean> SOCIAL_SPY = new PreferenceKeyImpl.BooleanKey(
            SOCIAL_SPY_KEY,
            true,
            ((serviceCollection, user) -> serviceCollection.permissionService().hasPermission(user, MessagePermissions.BASE_SOCIALSPY)
                    && !serviceCollection.permissionService().hasPermission(user, MessagePermissions.SOCIALSPY_FORCE)),
            "userpref.socialspy",
            MessageModule.ID
    );
    public static final PreferenceKeyImpl<Locale> PLAYER_LOCALE = new PreferenceKeyImpl.LocaleKey(
            PLAYER_LOCALE_KEY,
            Locale.UK,
            CorePermissions.BASE_NUCLEUSLANGUAGE,
            "userpref.player_locale",
            CoreModule.ID,
            (serviceCollection, uuid, value) -> serviceCollection.messageProvider().invalidateLocaleCacheFor(uuid)
    );

    public Collection<NucleusUserPreferenceService.PreferenceKey<?>> getAll() {
        final ImmutableSet.Builder<NucleusUserPreferenceService.PreferenceKey<?>> builder
                = ImmutableSet.builder();
        this.vanishOnLogin().ifPresent(builder::add);
        this.teleportTarget().ifPresent(builder::add);
        this.powertoolsEnabled().ifPresent(builder::add);
        this.socialSpyEnabled().ifPresent(builder::add);
        this.messageReceivingEnabled().ifPresent(builder::add);
        this.commandSpyEnabled().ifPresent(builder::add);
        this.viewStaffChat().ifPresent(builder::add);
        this.playerLocale().ifPresent(builder::add);
        return builder.build();
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> vanishOnLogin() {
        return VANISH_ON_LOGIN.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> teleportTarget() {
        return TELEPORT_TARGETABLE.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> powertoolsEnabled() {
        return POWERTOOL_ENABLED.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> socialSpyEnabled() {
        return SOCIAL_SPY.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> messageReceivingEnabled() {
        return RECEIVING_MESSAGES.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> commandSpyEnabled() {
        return COMMAND_SPY.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> viewStaffChat() {
        return VIEW_STAFF_CHAT.getIfLoaded(this.moduleDataProvider);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Locale>> playerLocale() {
        return Optional.of(PLAYER_LOCALE);
    }

}
