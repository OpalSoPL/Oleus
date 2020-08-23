/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import com.google.common.collect.ImmutableList;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import java.util.List;

import javax.annotation.Nullable;

public class PositiveDoubleArgument extends CommandElement {

    private final IMessageProviderService messageProvider;

    public PositiveDoubleArgument(@Nullable final TextComponent key, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        try {
            final double d = Double.parseDouble(args.next());
            if (d >= 0) {
                return d;
            }
        } catch (final NumberFormatException ignored) {}

        throw args.createError(this.messageProvider.getMessageFor(source, "args.positiveint.negative"));
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return ImmutableList.of();
    }
}
