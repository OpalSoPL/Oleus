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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;

import java.util.ArrayList;
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
        final WarpService handler = context.getServiceCollection().getServiceUnchecked(WarpService.class);
        Util.getPaginationBuilder(context.getAudience()).contents(
                handler.getWarpsWithCategories().keySet().stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(WarpCategory::getId)).map(x -> {
            final List<Component> t = new ArrayList<>();
            t.add(context.getMessage("command.warp.category.listitem.simple", Component.text(x.getId()), x.getDisplayName()));
            x.getDescription().ifPresent(y -> t.add(context.getMessage("command.warp.category.listitem.description", y)));
            return t;
        }).flatMap(Collection::stream).collect(Collectors.toList()))
        .title(context.getMessage("command.warp.category.listitem.title"))
        .padding(Component.text("-", NamedTextColor.GREEN))
        .sendTo(context.getAudience());

        return context.successResult();
    }
}
