/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.api.module.playerinfo.NucleusSeenService;
import io.github.nucleuspowered.nucleus.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerInformationService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import com.google.inject.Inject;

@APIService(NucleusSeenService.class)
public class SeenHandler implements NucleusSeenService, ServiceBase {

    private final INucleusServiceCollection serviceCollection;
    private final Map<String, List<SeenInformationProvider>> pluginInformationProviders = Maps.newTreeMap();

    @Inject
    public SeenHandler(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override
    public void register(@Nonnull final PluginContainer plugin, @Nonnull final SeenInformationProvider seenInformationProvider) throws IllegalArgumentException {
        Preconditions.checkNotNull(plugin);
        Preconditions.checkNotNull(seenInformationProvider);

        final Plugin pl = plugin.getClass().getAnnotation(Plugin.class);
        Preconditions.checkArgument(pl != null, this.serviceCollection.messageProvider().getMessage("seen.error.requireplugin"));

        final String name = pl.name();
        final List<SeenInformationProvider> providers;
        if (this.pluginInformationProviders.containsKey(name)) {
            providers = this.pluginInformationProviders.get(name);
        } else {
            providers = new ArrayList<>();
            this.pluginInformationProviders.put(name, providers);
        }

        providers.add(seenInformationProvider);
    }

    @Override
    public void register(final PluginContainer plugin, final Predicate<CommandSource> permissionCheck, final BiFunction<CommandSource, User, Collection<Text>> informationGetter)
        throws IllegalArgumentException {
        register(plugin, new SeenInformationProvider() {
            @Override public boolean hasPermission(@Nonnull final CommandSource source, @Nonnull final User user) {
                return permissionCheck.test(source);
            }

            @Override public Collection<Text> getInformation(@Nonnull final CommandSource source, @Nonnull final User user) {
                return informationGetter.apply(source, user);
            }
        });
    }

    public List<Text> getText(final CommandSource requester, final User user) {
        final List<Text> information = new ArrayList<>();

        final Collection<IPlayerInformationService.Provider> providers = this.serviceCollection.playerInformationService().getProviders();
        for (final IPlayerInformationService.Provider provider : providers) {
            provider.get(user, requester, this.serviceCollection).ifPresent(information::add);
        }

        for (final Map.Entry<String, List<SeenInformationProvider>> entry : this.pluginInformationProviders.entrySet()) {
            entry.getValue().stream().filter(sip -> sip.hasPermission(requester, user)).forEach(sip -> {
                final Collection<Text> input = sip.getInformation(requester, user);
                if (input != null && !input.isEmpty()) {
                    if (information.isEmpty()) {
                        information.add(Text.EMPTY);
                        information.add(Text.of("-----"));
                        information.add(this.serviceCollection.messageProvider().getMessageFor(requester, "seen.header.plugins"));
                        information.add(Text.of("-----"));
                    }

                    information.add(Text.EMPTY);
                    information.add(Text.of(TextColors.AQUA, entry.getKey() + ":"));
                    information.addAll(input);
                }
            });
        }

        return information;
    }
}
