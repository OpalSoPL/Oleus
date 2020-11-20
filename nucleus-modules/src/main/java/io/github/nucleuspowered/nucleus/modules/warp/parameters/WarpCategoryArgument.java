/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.parameters;

import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WarpCategoryArgument extends CommandElement {

    private final WarpService handler;
    private final IMessageProviderService messageProvider;

    public WarpCategoryArgument(@Nullable final TextComponent key, final INucleusServiceCollection serviceCollection, final WarpService handler) {
        super(key);
        this.handler = handler;
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Nullable @Override protected Object parseValue(@Nonnull final CommandSource source, @Nonnull final CommandArgs args) throws ArgumentParseException {
        final String arg = args.next();
        return this.handler.getWarpsWithCategories().keySet().stream().filter(Objects::nonNull).filter(x -> x.getId().equals(arg)).findFirst()
                .orElseThrow(() -> args.createError(
                    messageProvider.getMessageFor(source, "args.warpcategory.noexist", arg)));
    }

    @Nonnull @Override public List<String> complete(@Nonnull final CommandSource src, @Nonnull final CommandArgs args, @Nonnull final CommandContext context) {
        return this.handler.getWarpsWithCategories().keySet().stream().filter(Objects::nonNull).map(WarpCategory::getId).collect(Collectors.toList());
    }
}
