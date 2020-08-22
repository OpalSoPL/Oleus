/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;

import java.util.List;

import javax.annotation.Nullable;

public class IfConditionElseArgument extends CommandElement {

    private final Condition predicate;
    private final CommandElement trueElement;
    private final CommandElement falseElement;
    private final IPermissionService permissionService;

    public static IfConditionElseArgument permission(
            final IPermissionService permissionService,
            final String permission,
            final CommandElement ifSo,
            final CommandElement ifNot) {
        return new IfConditionElseArgument(permissionService, ifSo, ifNot, (p, s, c) -> p.hasPermission(s, permission));
    }

    public IfConditionElseArgument(
            final IPermissionService permissionService,
            final CommandElement trueElement,
            final CommandElement falseElement,
            final Condition predicate) {
        super(trueElement.getKey());
        this.permissionService = permissionService;
        this.trueElement = trueElement;
        this.falseElement = falseElement;
        this.predicate = predicate;
    }

    @Nullable @Override protected Object parseValue(final CommandSource source, final CommandArgs args) {
        return null;
    }

    @Override public void parse(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
        if (this.predicate.test(this.permissionService, source, context)) {
            this.trueElement.parse(source, args, context);
        } else {
            this.falseElement.parse(source, args, context);
        }
    }

    @Override public List<String> complete(final CommandSource source, final CommandArgs args, final CommandContext context) {
        if (this.predicate.test(this.permissionService, source, context)) {
            return this.trueElement.complete(source, args, context);
        } else {
            return this.falseElement.complete(source, args, context);
        }
    }

    @FunctionalInterface
    public interface Condition {

        boolean test(IPermissionService permissionService, CommandSource commandSource, CommandContext context);

    }
}
