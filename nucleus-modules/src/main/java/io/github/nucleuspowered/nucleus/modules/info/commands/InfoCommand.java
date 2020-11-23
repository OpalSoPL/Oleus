/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.io.TextFileController;
import io.github.nucleuspowered.nucleus.modules.info.InfoPermissions;
import io.github.nucleuspowered.nucleus.modules.info.config.InfoConfig;
import io.github.nucleuspowered.nucleus.modules.info.parameter.InfoValueParameter;
import io.github.nucleuspowered.nucleus.modules.info.services.InfoHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Command(
        aliases = {"info", "einfo"},
        basePermission = InfoPermissions.BASE_INFO,
        commandDescriptionKey = "info",
        associatedPermissions = InfoPermissions.INFO_LIST
)
@EssentialsEquivalent({"info", "ifo", "news", "about", "inform"})
public class InfoCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final InfoHandler infoService;
    private final Parameter.Value<InfoValueParameter.Result> parameter;

    private InfoConfig infoConfig = new InfoConfig();

    @Inject
    public InfoCommand(final INucleusServiceCollection serviceCollection) {
        this.infoService = serviceCollection.getServiceUnchecked(InfoHandler.class);
        this.parameter = Parameter.builder(InfoValueParameter.Result.class)
                .setKey("info")
                .optional()
                .parser(new InfoValueParameter(this.infoService, serviceCollection))
                .build();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.infoConfig = serviceCollection.configProvider().getModuleConfig(InfoConfig.class);
    }

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder()
                        .alias("l")
                        .alias("list")
                        .setRequirement(cause -> serviceCollection.permissionService().hasPermission(cause, InfoPermissions.INFO_LIST))
                        .build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.parameter
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        Optional<InfoValueParameter.Result> oir = context.getOne(this.parameter);
        if (this.infoConfig.isUseDefaultFile() && !oir.isPresent() && !context.hasFlag("l")) {
            // Do we have a default?
            final String def = this.infoConfig.getDefaultInfoSection();
            final Optional<TextFileController> list = this.infoService.getSection(def);
            if (list.isPresent()) {
                oir = Optional.of(new InfoValueParameter.Result(
                        this.infoService.getInfoSections().stream().filter(def::equalsIgnoreCase).findFirst().get(), list.get()));
            }
        }

        if (oir.isPresent()) {
            final TextFileController controller = oir.get().text;
            final Component def = LegacyComponentSerializer.legacyAmpersand().deserialize(oir.get().name);
            final Component title = context.getMessage("command.info.title.section",
                    controller.getTitle(context.getAudience()).orElse(def));

            controller.sendToAudience(context.getAudience(), title);
            return context.successResult();
        }

        // Create a list of pages to load.
        final Set<String> sections = this.infoService.getInfoSections();
        if (sections.isEmpty()) {
            return context.errorResult("command.info.none");
        }

        // Create the text.
        final List<Component> s = new ArrayList<>();
        sections.forEach(x -> {
            final TextComponent.Builder tb = Component.text().append(
                    Component.text().content(x)
                        .color(NamedTextColor.GREEN).style(Style.style(TextDecoration.ITALIC))
                        .hoverEvent(HoverEvent.showText(context.getMessage("command.info.hover", x)))
                        .clickEvent(ClickEvent.runCommand("/nucleus:info " + x)).build());

            // If there is a title, then add it.
            this.infoService.getSection(x).get().getTitle(context.getAudience()).ifPresent(sub ->
                tb.append(Component.text(" - ").color(NamedTextColor.GOLD)).append(sub)
            );

            s.add(tb.build());
        });

        Util.getPaginationBuilder(context.getAudience()).contents()
                .header(context.getMessage("command.info.header.default"))
                .title(context.getMessage("command.info.title.default"))
                .contents(s.stream().sorted(Comparator.comparing(x -> PlainComponentSerializer.plain().serialize(x))).collect(Collectors.toList()))
                .padding(Component.text().content("-").color(NamedTextColor.GOLD).build())
                .sendTo(context.getAudience());
        return context.successResult();
    }
}
