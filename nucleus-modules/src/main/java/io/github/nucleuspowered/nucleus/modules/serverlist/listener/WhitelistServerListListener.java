/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.serverlist.listener;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.modules.serverlist.config.ServerListConfig;
import io.github.nucleuspowered.nucleus.modules.serverlist.services.ServerListService;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.server.ClientPingServerEvent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.inject.Inject;

public class WhitelistServerListListener implements IReloadableService.Reloadable, ListenerBase.Conditional {

    private final ServerListService service;
    private final SecureRandom random = new SecureRandom(); // stop complaining SonarQube
    private final List<NucleusTextTemplate> whitelist = new ArrayList<>();

    @Inject
    public WhitelistServerListListener(final INucleusServiceCollection serviceCollection) {
        this.service = serviceCollection.getServiceUnchecked(ServerListService.class);
    }

    @Listener(order = Order.LATE)
    public void onServerListPing(final ClientPingServerEvent event, @Getter("response") final ClientPingServerEvent.Response response) {
        if (!Sponge.server().isWhitelistEnabled()) {
            return;
        }

        final Optional<Component> ott = this.service.getMessage();
        if (!ott.isPresent() &&  !this.whitelist.isEmpty()) {
            final NucleusTextTemplate template = this.whitelist.get(this.random.nextInt(this.whitelist.size()));
            response.setDescription(template.getForObject(Sponge.systemSubject()));
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.whitelist.clear();
        serviceCollection.configProvider().getModuleConfig(ServerListConfig.class)
                .getWhitelist()
                .stream()
                .map(x -> serviceCollection.textTemplateFactory().createFromAmpersandStringIgnoringExceptions(x).orElse(null))
                .filter(Objects::nonNull)
                .forEach(this.whitelist::add);
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(ServerListConfig.class).enableWhitelistListener();
    }
}
