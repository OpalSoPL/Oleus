/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.warp.commands.DeleteWarpCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.ListWarpCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.SetCategoryCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.SetCostCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.SetDescriptionCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.SetWarpCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.WarpCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.category.CategoryCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.category.CategoryDescriptionCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.category.CategoryDisplayNameCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.category.CategoryRemoveDescriptionCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.category.CategoryRemoveDisplayNameCommand;
import io.github.nucleuspowered.nucleus.modules.warp.commands.category.ListCategoryCommand;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpCategorySerialiser;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpConfig;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpSerialiser;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class WarpModule implements IModule.Configurable<WarpConfig> {

    public static final String ID = "warp";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(WarpService.class, new WarpService(serviceCollection), false);
        serviceCollection.configurateHelper().addTypeSerialiser(TypeSerializerCollection.builder()
                .register(WarpCategory.class, new WarpCategorySerialiser())
                .register(Warp.class, WarpSerialiser.INSTANCE)
                .build());
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                CategoryCommand.class,
                CategoryDescriptionCommand.class,
                CategoryDisplayNameCommand.class,
                CategoryRemoveDescriptionCommand.class,
                CategoryRemoveDisplayNameCommand.class,
                ListCategoryCommand.class,
                DeleteWarpCommand.class,
                ListWarpCommand.class,
                SetCategoryCommand.class,
                SetCostCommand.class,
                SetDescriptionCommand.class,
                SetWarpCommand.class,
                WarpCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(WarpPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override public Class<WarpConfig> getConfigClass() {
        return WarpConfig.class;
    }
}
