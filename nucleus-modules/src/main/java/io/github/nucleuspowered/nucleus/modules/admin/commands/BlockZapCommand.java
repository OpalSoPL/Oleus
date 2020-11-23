/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.modules.admin.AdminPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommonParameters;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.ServerLocation;

@Command(aliases = {"blockzap", "zapblock"},
        basePermission = AdminPermissions.BASE_BLOCKZAP,
        commandDescriptionKey = "blockzap",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = AdminPermissions.EXEMPT_WARMUP_BLOCKZAP),
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = AdminPermissions.EXEMPT_COOLDOWN_BLOCKZAP),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = AdminPermissions.EXEMPT_COST_BLOCKZAP)
        }
)
@EssentialsEquivalent(value = "break", isExact = false, notes = "Requires co-ordinates, whereas Essentials required you to look at the block.")
public class BlockZapCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                CommonParameters.LOCATION_ONLINE_ONLY
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerLocation location = context.requireOne(CommonParameters.LOCATION_ONLINE_ONLY.getKey());
        if (!location.getBlockType().getItem().isPresent()) {
            return context.errorResult("command.blockzap.alreadyair", location.getPosition().toString(), location.getWorldKey().asString());
        }

        final BlockType type = location.getBlockType();
        final Component itemTextComponent = type.asComponent();

        try (final CauseStackManager.StackFrame frame = Sponge.getServer().getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(context.getCommandSourceRoot());
            if (location.setBlock(BlockTypes.AIR.get().getDefaultState(), BlockChangeFlags.ALL)) {
                context.sendMessage("command.blockzap.success", Component.text(location.getPosition().toString()),
                        Component.text(location.getWorldKey().getFormatted()),
                        itemTextComponent);
                return context.successResult();
            }
        }

        return context.errorResult("command.blockzap.fail", location.getPosition().toString(), location.getWorldKey().getFormatted());
    }
}
