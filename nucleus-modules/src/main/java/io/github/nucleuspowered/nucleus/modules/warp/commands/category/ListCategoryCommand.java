/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands.category;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.modules.warp.WarpPermissions;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Command(
        aliases = "list",
        basePermission = WarpPermissions.BASE_CATEGORY_LIST,
        commandDescriptionKey = "warp.category.list",
        parentCommand = CategoryCommand.class
)
public class ListCategoryCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get all the categories.
        final CommandSource src = context.getCommandSourceRoot();
        final WarpService handler = context.getServiceCollection().getServiceUnchecked(WarpService.class);
        Util.getPaginationBuilder(src).contents(
                handler.getWarpsWithCategories().keySet().stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(WarpCategory::getId)).map(x -> {
            final List<Text> t = new ArrayList<>();
            t.add(context.getMessage("command.warp.category.listitem.simple", Text.of(x.getId()), x.getDisplayName()));
            x.getDescription().ifPresent(y -> t.add(context.getMessage("command.warp.category.listitem.description", y)));
            return t;
        }).flatMap(Collection::stream).collect(Collectors.toList()))
        .title(context.getMessage("command.warp.category.listitem.title"))
        .padding(Text.of("-", TextColors.GREEN))
        .sendTo(src);

        return context.successResult();
    }
}
