/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImprovedCatalogTypeArgument extends CommandElement {

    private final Class<? extends CatalogType> type;
    private final IMessageProviderService messageProviderService;

    public ImprovedCatalogTypeArgument(@Nonnull final Text key, final Class<? extends CatalogType> type, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.type = type;
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String arg = args.next().toLowerCase();

        // Try
        final GameRegistry registry = Sponge.getRegistry();
        Optional<? extends CatalogType> catalogType = registry.getType(this.type, arg);
        if (!catalogType.isPresent() && !arg.contains(":")) {
            catalogType = registry.getType(this.type, "minecraft:" + arg);
            if (!catalogType.isPresent()) {
                catalogType = registry.getType(this.type, "sponge:" + arg);
            }
        }

        if (catalogType.isPresent()) {
            return catalogType.get();
        }

        throw args.createError(this.messageProviderService.getMessageFor(source, "args.catalogtype.nomatch", arg));
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        try {
            final String arg = args.peek().toLowerCase();
            return Sponge.getRegistry().getAllOf(this.type).stream()
                    .filter(x -> x.getId().startsWith(arg) || x.getId().startsWith("minecraft:" + arg) || x.getId().startsWith("sponge:" + arg))
                    .map(CatalogType::getId).collect(Collectors.toList());
        } catch (final Exception e) {
            return Sponge.getRegistry().getAllOf(this.type).stream().map(CatalogType::getId).collect(Collectors.toList());
        }
    }
}
