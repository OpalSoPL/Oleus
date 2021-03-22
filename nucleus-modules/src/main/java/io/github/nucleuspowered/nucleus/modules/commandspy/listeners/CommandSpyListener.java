/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandspy.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyPermissions;
import io.github.nucleuspowered.nucleus.modules.commandspy.config.CommandSpyConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.ITextStyleService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IUserPreferenceService;
import io.github.nucleuspowered.nucleus.core.util.CommandNameCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandSpyListener implements IReloadableService.Reloadable, ListenerBase.Conditional {

    private final IPermissionService permissionService;
    private final IUserPreferenceService userPreferenceService;
    private final ITextStyleService textStyleService;
    private CommandSpyConfig config = new CommandSpyConfig();
    private Set<String> toSpy = Collections.emptySet();
    private boolean listIsEmpty = true;
    private NucleusTextTemplate prefix = NucleusTextTemplateImpl.empty();

    @Inject
    public CommandSpyListener(final INucleusServiceCollection serviceCollection) {
        this.permissionService = serviceCollection.permissionService();
        this.userPreferenceService = serviceCollection.userPreferenceService();
        this.textStyleService = serviceCollection.textStyleService();
    }

    @Listener(order = Order.LAST)
    public void onCommand(final ExecuteCommandEvent.Pre event, @Root final ServerPlayer player) {

        if (!this.permissionService.hasPermission(player, CommandSpyPermissions.COMMANDSPY_EXEMPT_TARGET)) {
            boolean isInList = false;
            if (!this.listIsEmpty) {
                final String command = event.getCommand().toLowerCase();
                final Set<String> cmd = CommandNameCache.INSTANCE.getFromCommandAndSource(command, event.getCommandCause());
                cmd.retainAll(this.toSpy);
                isInList = !cmd.isEmpty();
            }

            // If the command is in the list, report it.
            if (isInList == this.config.isUseWhitelist()) {
                final UUID currentUUID = player.getUniqueId();
                final List<Player> playerList = Sponge.server().onlinePlayers()
                    .stream()
                    .filter(x -> !x.getUniqueId().equals(currentUUID))
                    .filter(x -> this.permissionService.hasPermission(x, CommandSpyPermissions.BASE_COMMANDSPY))
                    .filter(x -> this.userPreferenceService
                            .getUnwrapped(x.getUniqueId(), this.userPreferenceService.keys().commandSpyEnabled().get()))
                    .collect(Collectors.toList());

                if (!playerList.isEmpty()) {
                    final Component prefix = this.prefix.getForObject(player);
                    final ITextStyleService.TextFormat st = this.textStyleService.getLastColourAndStyle(prefix, null);
                    final Component messageToSend = LinearComponents.linear(
                            prefix,
                            Component.text()
                                    .color(st.colour().orElse(null))
                                    .style(st.style())
                                    .content("/" + event.getCommand() + " " + event.getArguments())
                                    .build()
                    );
                    playerList.forEach(x -> x.sendMessage(messageToSend));
                }
            }
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.config = serviceCollection.configProvider().getModuleConfig(CommandSpyConfig.class);
        this.listIsEmpty = this.config.getCommands().isEmpty();
        this.toSpy = this.config.getCommands().stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.prefix =
                serviceCollection.textTemplateFactory().createFromAmpersandStringIgnoringExceptions(this.config.getTemplate()).orElseGet(NucleusTextTemplateImpl::empty);
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return !this.config.isUseWhitelist() || !this.listIsEmpty;
    }
}
