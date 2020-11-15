/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus;

import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.RegexParameter;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.UUIDParameter;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.util.GeAnTyRefTypeTokens;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileManager;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Command(
        aliases = "getuser",
        basePermission = CorePermissions.BASE_NUCLEUS_GETUSER,
        commandDescriptionKey = "nucleus.getuser",
        parentCommand = NucleusCommand.class
)
public class GetUserCommand implements ICommandExecutor {

    private final Parameter.Key<UUID> uuidKey = Parameter.key("UUID", GeAnTyRefTypeTokens.UUID);
    private final Parameter.Key<String> playerKey = Parameter.key("name", GeAnTyRefTypeTokens.STRING);

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                Parameter.firstOf(
                        Parameter.builder(UUID.class).parser(new UUIDParameter<>(Optional::ofNullable, serviceCollection.messageProvider())).setKey(this.uuidKey).build(),
                        Parameter.builder(String.class).parser(new RegexParameter(Pattern.compile("^[.]{1,16}$"), "command.nucleus.getuser.regex",
                                serviceCollection.messageProvider())).build()
            )
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) {
        final CompletableFuture<GameProfile> profile;
        final String toGet;
        final GameProfileManager manager = Sponge.getServer().getGameProfileManager();
        if (context.hasAny(this.uuidKey)) {
            final UUID u = context.requireOne(this.uuidKey);
            toGet = u.toString();
            profile = manager.getProfile(u, false);
        } else {
            toGet = context.requireOne(this.playerKey);
            profile = manager.getProfile(toGet, false);
        }

        context.sendMessage("command.nucleus.getuser.starting", toGet);

        profile.handle((gp, th) -> {
            if (th != null || gp == null) {
                if (th != null) {
                    th.printStackTrace();
                }

                context.sendMessage("command.nucleus.getuser.failed", toGet);
                return 0; // I have to return something, even though I don't care about it.
            }

            // We have a game profile, it's been added to the cache. Create the user too, just in case.
            Sponge.getServer().getUserManager().getOrCreate(gp);
            context.sendMessage("command.nucleus.getuser.success", gp.getUniqueId().toString(), gp.getName().orElse("unknown"));

            return 0;
        });


        return context.successResult();
    }
}
