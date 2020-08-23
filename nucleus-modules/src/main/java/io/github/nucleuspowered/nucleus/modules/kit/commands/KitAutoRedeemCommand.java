/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfig;
import io.github.nucleuspowered.nucleus.modules.kit.parameters.KitParameter;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
/**
 * Sets kit to be automatically redeemed on login.
 */
@Command(
        aliases = { "autoredeem" },
        basePermission = KitPermissions.BASE_KIT_AUTOREDEEM,
        commandDescriptionKey = "kit.autoredeem",
        parentCommand = KitCommand.class,
        async = true
)
public class KitAutoRedeemCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private boolean autoRedeemEnabled = false;

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.getServiceUnchecked(KitService.class).createKitElement(true),
                NucleusParameters.ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kitInfo = context.requireOne(KitParameter.KIT_PARAMETER_KEY, Kit.class);
        final boolean b = context.requireOne(NucleusParameters.Keys.BOOL, Boolean.class);

        // This Kit is a reference back to the version in list, so we don't need
        // to update it explicitly
        kitInfo.setAutoRedeem(b);
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kitInfo);
        context.sendMessage(b ? "command.kit.autoredeem.on" : "command.kit.autoredeem.off", kitInfo.getName());
        if (!this.autoRedeemEnabled) {
            context.sendMessage("command.kit.autoredeem.disabled");
        }

        return context.successResult();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.autoRedeemEnabled = serviceCollection.moduleDataProvider().getModuleConfig(KitConfig.class).isEnableAutoredeem();
    }
}
