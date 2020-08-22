/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

import com.google.common.collect.ImmutableList;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.util.WrappedElement;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;

import javax.annotation.Nullable;

public class NucleusRequirePermissionArgument extends WrappedElement {

    private final String permission;
    private final IPermissionService permissionService;
    private final boolean isOptional;

    public NucleusRequirePermissionArgument(
            final CommandElement wrapped, final IPermissionService permissionService, final String permission, final boolean isOptional) {
        super(wrapped);
        this.permissionService = permissionService;
        this.permission = permission;
        this.isOptional = isOptional;
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        return null;
    }

    @Override
    public void parse(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
        if (!this.permissionService.hasPermission(source, this.permission)) {
            if (this.isOptional) {
                return;
            }
            final Text key = this.getKey();
            throw args.createError(t("You do not have permission to use the %s argument", key != null ? key : t("unknown")));
        }
        this.getWrappedElement().parse(source, args, context);
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        if (!this.permissionService.hasPermission(src, this.permission)) {
            return ImmutableList.of();
        }
        return this.getWrappedElement().complete(src, args, context);
    }
}
