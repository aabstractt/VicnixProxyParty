package me.heyimblake.proxyparty.commands;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class PartySubCommand {

    public abstract void execute(ProxiedPlayer player, String[] args);

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

    public List<String> getComplete(String[] args) {
        return new ArrayList<>();
    }
}