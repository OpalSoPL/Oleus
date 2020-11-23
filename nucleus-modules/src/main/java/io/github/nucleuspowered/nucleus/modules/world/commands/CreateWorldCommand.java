/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.config.WorldConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.dimension.DimensionType;
import org.spongepowered.api.world.gen.GeneratorModifierType;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Command(
        aliases = {"create"},
        basePermission = WorldPermissions.BASE_WORLD_CREATE,
        commandDescriptionKey = "world.create",
        parentCommand = WorldCommand.class
)
public class CreateWorldCommand implements ICommandExecutor, IReloadableService.Reloadable {

    @Nullable private Long worldBorderDefault;

    private final Parameter.Value<WorldArchetype> presetValue = Parameter.catalogedElement(WorldArchetype.class)
            .setKey("world preset")
            .build();
    private final Parameter.Value<DimensionType> dimensionType = Parameter.catalogedElement(DimensionType.class)
            .setKey("dimension type")
            .build();
    private final Parameter.Value<Difficulty> difficultyValue = Parameter.catalogedElement(Difficulty.class).setKey("difficulty").build();
    private final Parameter.Value<GeneratorModifierType> generatorModifierTypeValue = Parameter.catalogedElement(GeneratorModifierType.class)
            .setKey("modifier type")
            .build();
    private final Parameter.Value<GameMode> gameMode = Parameter.catalogedElement(GameMode.class).setKey("gamemode").build();
    private final Parameter.Value<Long> seedValue = Parameter.longNumber().setKey("seed").build();
    private final Parameter.Value<Boolean> loadOnStartup = Parameter.bool().setKey("loadOnStartup").build();
    private final Parameter.Value<Boolean> keepSpawnLoaded = Parameter.bool().setKey("keepSpawnLoaded").build();
    private final Parameter.Value<Boolean> allowCommands = Parameter.bool().setKey("allowCommands").build();
    private final Parameter.Value<Boolean> structures = Parameter.bool().setKey("structures").build();
    private final Parameter.Value<Boolean> bonusChest = Parameter.bool().setKey("bonusChest").build();
    private final Parameter.Value<String> nameValue = Parameter.string().setKey("name").build();
    private final Parameter.Value<Boolean> pvpEnabled = Parameter.bool().setKey("pvp").build();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of(this.allowCommands, "c", "allowcommands"),
                Flag.of(this.bonusChest, "b", "bonuschest"),
                Flag.of(this.presetValue, "p", "preset"),
                Flag.of(this.dimensionType, "di", "dimension"),
                Flag.of(this.difficultyValue, "d", "difficulty"),
                Flag.of(this.generatorModifierTypeValue, "m", "modifier"),
                Flag.of(this.gameMode, "gm", "gamemode"),
                Flag.of(this.seedValue, "s", "seed"),
                Flag.of(this.loadOnStartup, "l", "loadonstartup"),
                Flag.of(this.structures, "st", "structures"),
                Flag.of(this.keepSpawnLoaded, "k", "keepspawnloaded"),
                Flag.of(this.pvpEnabled, "pvp")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.nameValue
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String nameInput = context.requireOne(this.nameValue);
        final Optional<DimensionType> dimensionInput = context.getOne(this.dimensionType);
        final Optional<GameMode> gamemodeInput = context.getOne(this.gameMode);
        final Optional<Difficulty> difficultyInput = context.getOne(this.difficultyValue);
        final Optional<GeneratorModifierType> modifiers = context.getOne(this.generatorModifierTypeValue);
        final Optional<Long> seedInput = context.getOne(this.seedValue);
        final Optional<WorldArchetype> preset = context.getOne(this.presetValue);
        final boolean genStructures = context.getOne(this.structures).orElse(true);
        final boolean loadOnStartup = context.getOne(this.loadOnStartup).orElse(true);
        final boolean keepSpawnLoaded = context.getOne(this.keepSpawnLoaded).orElse(true);
        final boolean allowCommands = context.getOne(this.allowCommands).orElse(true);
        final boolean bonusChest = context.getOne(this.bonusChest).orElse(true);
        final boolean pvp = context.getOne(this.pvpEnabled).orElse(true);

        final ResourceKey projectedKey = ResourceKey.of(context.getServiceCollection().pluginContainer(), nameInput.toLowerCase());
        if (Sponge.getServer().getWorldManager().getProperties(projectedKey).isPresent()) {
            return context.errorResult("command.world.create.exists", nameInput);
        }

        // Generate preset key.
        final ResourceKey worldKey = ResourceKey.of(context.getServiceCollection().pluginContainer(), nameInput.toLowerCase());
        ResourceKey toSet = worldKey;
        int i = 0;
        while (!Sponge.getRegistry().getCatalogRegistry().get(WorldArchetype.class, toSet).isPresent()) {
            ++i;
            toSet = ResourceKey.of(context.getServiceCollection().pluginContainer(), nameInput.toLowerCase() + i);
        }

        final WorldArchetype.Builder worldSettingsBuilder = WorldArchetype.builder().enabled(true);
        preset.ifPresent(worldSettingsBuilder::from);
        dimensionInput.ifPresent(worldSettingsBuilder::dimensionType);
        gamemodeInput.ifPresent(worldSettingsBuilder::gameMode);
        difficultyInput.ifPresent(worldSettingsBuilder::difficulty);
        modifiers.ifPresent(worldSettingsBuilder::generatorModifierType);

        worldSettingsBuilder.loadOnStartup(loadOnStartup)
                .keepSpawnLoaded(keepSpawnLoaded)
                .generateStructures(genStructures)
                .commandsEnabled(allowCommands)
                .generateBonusChest(bonusChest)
                .pvpEnabled(pvp);
        seedInput.ifPresent(worldSettingsBuilder::seed);

        final WorldArchetype wa = worldSettingsBuilder.key(toSet).build();

        context.sendMessage("command.world.create.begin", nameInput);
        context.sendMessage("command.world.create.newparams",
                wa.getDimensionType().key().asString(),
                wa.getGeneratorModifier().getKey().asString(),
                wa.getGameMode().getKey().asString(),
                wa.getDifficulty().asComponent());
        context.sendMessage("command.world.create.newparams2",
                String.valueOf(loadOnStartup),
                String.valueOf(keepSpawnLoaded),
                String.valueOf(genStructures),
                String.valueOf(allowCommands),
                String.valueOf(bonusChest));

        Sponge.getGame().getServer().getWorldManager().createProperties(worldKey, wa).handle((worldProperties, exception) -> {
            if (exception != null) {
                context.getServiceCollection().schedulerService().runOnMainThread(
                        () -> context.sendMessageText(Component.text(exception.getMessage())));
                final CompletableFuture<ServerWorld> worldCompletableFuture = new CompletableFuture<>();
                worldCompletableFuture.complete(null);
                return worldCompletableFuture;
            }

            if (this.worldBorderDefault != null && this.worldBorderDefault > 0) {
                worldProperties.getWorldBorder().setDiameter(this.worldBorderDefault);
            }

            return Sponge.getServer().getWorldManager().loadWorld(worldProperties);
        }).<Void>handle((world, exception) -> {
            if (exception != null) {
                context.getServiceCollection().schedulerService().runOnMainThread(
                        () -> context.sendMessage("command.world.create.worldfailedtoload", nameInput));
                return null;
            }

            if (world != null) {
                context.getServiceCollection().schedulerService().runOnMainThread(
                        () -> context.sendMessage("command.world.create.success", nameInput));
            }
            return null;
        });
        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.worldBorderDefault = serviceCollection
                .configProvider()
                .getModuleConfig(WorldConfig.class)
                .getWorldBorderDefault()
                .orElse(null);
    }

}
