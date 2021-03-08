package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

@PartyAnnotationCommand(
        name = "invitaciones",
        syntax = "/party invitaciones",
        description = "Mira la lista de los jugadores invitados a tu party!.",
        requiresArgumentCompletion = false
)
public class InvitedSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party.getInvited().size() != 0) {
            List<BaseComponent> names = new ArrayList<>();

            for (ProxiedPlayer invited : party.getInvited()) {
                TextComponent textComponent = new TextComponent(invited.getName());

                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party removerinvitacion " + invited.getName()));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.YELLOW + "Click para remover invitacion de "+ ChatColor.GREEN + invited.getName() + ".")}));
                textComponent.setColor(ChatColor.DARK_AQUA);

                names.add(textComponent);
            }

            player.sendMessage(ChatColor.GREEN + "Lista de Jugadores Invitados:");

            names.forEach(name -> player.sendMessage(Constants.TAG, name));

            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW+"Para eliminar una invitación haz click encima del nombre.");

            return;
        }

        player.sendMessage(Constants.TAG, new ComponentBuilder("No tienes invitaciones enviados.").color(ChatColor.RED).create()[0]);
    }
}
