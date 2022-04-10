package io.github.nucleuspowered.docgen.module;

import com.google.inject.Inject;
import io.github.nucleuspowered.docgen.module.command.DocGenCommand;
import io.github.nucleuspowered.docgen.module.service.DocumentationGenerationService;
import io.github.nucleuspowered.nucleus.core.NucleusJavaProperties;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class DocgenModule implements IModule {

    private final INucleusServiceCollection serviceCollection;
    private final Game game;

    @Inject
    public DocgenModule(final INucleusServiceCollection serviceCollection, final Game game) {
        this.serviceCollection = serviceCollection;
        this.game = game;
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singleton(DocGenCommand.class);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(DocgenPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Listener
    public void onServerStarted(final StartedEngineEvent<Server> event) {
        if (NucleusJavaProperties.DOCGEN_PATH != null) {
            final Path finalPath;
            try {
                final String docgenPath = NucleusJavaProperties.DOCGEN_PATH;
                if (docgenPath.isEmpty()) {
                    finalPath = this.serviceCollection.dataDir().get();
                } else {
                    final Path path = this.game.gameDirectory().resolve(docgenPath);
                    boolean isOk = path.toAbsolutePath().startsWith(this.game.gameDirectory().toAbsolutePath());
                    isOk &= Files.notExists(path) || Files.isDirectory(path);
                    if (isOk) {
                        Files.createDirectories(path);
                        finalPath = path;
                    } else {
                        finalPath = this.serviceCollection.dataDir().get();
                    }
                }
                this.serviceCollection.logger().info("Starting generation of documentation, saving files to: {}", finalPath.toString());
                DocumentationGenerationService.Holder.INSTANCE.generate(finalPath, this.serviceCollection);
                this.serviceCollection.logger().info("Generation is complete. Server will shut down.");
            } catch (final Exception ex) {
                this.serviceCollection.logger().error("Could not generate. Server will shut down.");
                ex.printStackTrace();
            }

            this.game.server().shutdown();
        }
    }
}
