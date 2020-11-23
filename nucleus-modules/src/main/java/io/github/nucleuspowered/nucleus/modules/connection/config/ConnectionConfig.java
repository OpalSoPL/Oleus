/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.connection.config;

import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Optional;

@ConfigSerializable
public class ConnectionConfig {

    @Setting(value = "reserved-slots")
    @LocalisedComment("config.connection.reservedslots")
    private int reservedSlots = -1;

    @Setting(value = "whitelist-message")
    @LocalisedComment("config.connection.whitelistmessage")
    private String whitelistMessage = "";

    @Setting(value = "server-full-message")
    @LocalisedComment("config.connection.serverfullmessage")
    private String serverFullMessage = "";

    public int getReservedSlots() {
        return this.reservedSlots;
    }

    public Optional<Component> getWhitelistMessage() {
        return this.getMessageFrom(this.whitelistMessage);
    }

    public Optional<Component> getServerFullMessage() {
        return this.getMessageFrom(this.serverFullMessage);
    }

    private Optional<Component> getMessageFrom(final String text) {
        if (text == null || text.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
    }

}
