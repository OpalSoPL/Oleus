/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.staffchat.config.StaffChatConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;

import java.util.Collection;
import java.util.Optional;

public class StaffChatModule implements IModule.Configurable<StaffChatConfig> {

    public static final String ID = "staff-chat";

    @Override public void init(final INucleusServiceCollection serviceCollection) {
        new StaffChatMessageChannel(serviceCollection);
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }

    @Override public Collection<Class<? extends TaskBase>> getTasks() {
        return null;
    }

    @Override public Class<StaffChatConfig> getConfigClass() {
        return StaffChatConfig.class;
    }

    @Listener
    public void onRegisterNucleusPreferenceKeys(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(
                new PreferenceKeyImpl.BooleanKey(
                        NucleusKeysProvider.VIEW_STAFF_CHAT_KEY,
                        true,
                        StaffChatPermissions.BASE_STAFFCHAT,
                        "userpref.viewstaffchat"
                )
        );
    }
}
