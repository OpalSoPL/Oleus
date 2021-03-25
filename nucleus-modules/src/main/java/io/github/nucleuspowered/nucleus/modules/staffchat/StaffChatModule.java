/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.core.event.NucleusRegisterPreferenceKeyEvent;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IUserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.staffchat.commands.StaffChatCommand;
import io.github.nucleuspowered.nucleus.modules.staffchat.commands.ToggleStaffChatCommand;
import io.github.nucleuspowered.nucleus.modules.staffchat.config.StaffChatConfig;
import io.github.nucleuspowered.nucleus.modules.staffchat.services.StaffChatService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class StaffChatModule implements IModule.Configurable<StaffChatConfig> {

    public static final String ID = "staff-chat";

    private final IUserPreferenceService preferenceService;

    @Inject
    public StaffChatModule(final IUserPreferenceService userPreferenceService) {
        this.preferenceService = userPreferenceService;
    }

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        new StaffChatMessageChannel(serviceCollection);
        serviceCollection.registerService(StaffChatService.class, new StaffChatService(serviceCollection), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                StaffChatCommand.class,
                ToggleStaffChatCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(StaffChatPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override public Class<StaffChatConfig> getConfigClass() {
        return StaffChatConfig.class;
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final NucleusRegisterPreferenceKeyEvent event) {
        event.register(StaffChatKeys.VIEW_STAFF_CHAT);
    }

}
