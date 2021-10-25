package me.heyimblake.proxyparty.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public abstract class PartySubCommand {

    public abstract void execute(ProxiedPlayer player, String[] args);

    protected boolean checkTargetOnline(UUID targetUniqueId, ProxiedPlayer sender) {
        if (targetUniqueId == null) {
            sender.sendMessage(new ComponentBuilder("El jugador no se pudo encontrar.").color(ChatColor.RED).create());

            return true;
        }

        return false;
    }

    /**
     * Gets the Annotation of a PartyAnnotationCommand class.
     *
     * @return Annotation if it exists, null if invalid
     */
    public PartyAnnotationCommand getAnnotations() {
        if (this.getClass().isAnnotationPresent(PartyAnnotationCommand.class)) {
            return this.getClass().getAnnotation(PartyAnnotationCommand.class);
        }

        return null;
    }
}