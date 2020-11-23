/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.IPluginInfo;
import io.github.nucleuspowered.nucleus.core.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IModuleReporter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;

import java.util.Collection;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

@Command(
        aliases = "nucleus",
        basePermission = CorePermissions.BASE_NUCLEUS,
        commandDescriptionKey = "nucleus",
        prefixAliasesWithN = false
)
public class NucleusCommand implements ICommandExecutor {

    private final IModuleReporter reporter;
    private final Component version;

    @Inject
    public NucleusCommand(final IModuleReporter reporter, final IPluginInfo pluginInfo) {
        this.reporter = reporter;
        this.version = Component.text()
                .color(NamedTextColor.GREEN)
                .content(pluginInfo.name() + " version " + pluginInfo.version() + " (built from commit " + pluginInfo.gitHash() + ")")
                .build();
    }
    @Nullable private Component modules = null;

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        if (this.modules == null) {
            final TextComponent.Builder tb = Component.text().content("Modules: ").color(NamedTextColor.GREEN);

            boolean addComma = false;
            final Collection<String> modules = this.reporter.discoveredModules().stream().sorted().collect(Collectors.toList());
            final Collection<String> enabled = this.reporter.enabledModules().stream().map(ModuleContainer::getId).collect(Collectors.toList());
            for (final String module : modules) {
                if (addComma) {
                    tb.append(Component.text(", ", NamedTextColor.GREEN));
                }

                tb.append(Component.text(module, enabled.contains(module) ? NamedTextColor.GREEN : NamedTextColor.RED));
                addComma = true;
            }

            this.modules = tb.append(Component.text(".", NamedTextColor.GREEN)).build();
        }

        context.sendMessageText(this.version);
        context.sendMessageText(this.modules);
        return context.successResult();
    }
}
