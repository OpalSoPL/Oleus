/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import io.github.nucleuspowered.nucleus.modules.warp.WarpPermissions;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpConfig;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.PositiveDoubleArgument;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
@Command(
        aliases = {"cost", "setcost"},
        basePermission = WarpPermissions.BASE_WARP_COST,
        commandDescriptionKey = "warp.cost",
        async = true,
        parentCommand = WarpCommand.class
)
public class SetCostCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final String costKey = "cost";
    private double defaultCost = 0;

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.getServiceUnchecked(WarpService.class).warpElement(false),
                GenericArguments.onlyOne(new PositiveDoubleArgument(Text.of(this.costKey), serviceCollection))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Warp warpData = context.requireOne(WarpService.WARP_KEY, Warp.class);
        final double cost = context.requireOne(this.costKey, Double.class);
        if (cost < -1) {
            return context.errorResult("command.warp.costset.arg");
        }

        final WarpService warpService = context.getServiceCollection().getServiceUnchecked(WarpService.class);
        if (cost == -1 && warpService.setWarpCost(warpData.getName(), -1)) {
            context.sendMessage("command.warp.costset.reset", warpData.getName(), String.valueOf(this.defaultCost));
            return context.successResult();
        } else if (warpService.setWarpCost(warpData.getName(), cost)) {
            context.sendMessage("command.warp.costset.success", warpData.getName(), cost);
            return context.successResult();
        }

        return context.errorResult("command.warp.costset.failed", warpData.getName());
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.defaultCost = serviceCollection.moduleDataProvider()
                .getModuleConfig(WarpConfig.class)
                .getDefaultWarpCost();
    }

}
