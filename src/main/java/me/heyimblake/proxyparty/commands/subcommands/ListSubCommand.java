package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "lista",
        syntax = "/party lista",
        description = "Miembros de la party.",
        requiresArgumentCompletion = false
)
public class ListSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        TextComponent line1 = new TextComponent("Lider de la Party: ");

        line1.setColor(ChatColor.YELLOW);
        line1.setBold(true);
        line1.addExtra(new TextComponent(party.getLeader().getName()));

        if (party.getParticipants().size() != 0) {
            TextComponent line2 = new TextComponent("Miembros de la Party");

            line2.setColor(ChatColor.AQUA);

            if (Constants.MAX_PARTY_SIZE != -1) {
                TextComponent count = new TextComponent(" (" + party.getParticipants().size() + "/" + Constants.MAX_PARTY_SIZE + ")");

                count.setColor(ChatColor.AQUA);
                line2.addExtra(count);
            }

            String allParticipants = "";

            for (ProxiedPlayer participant : party.getParticipants()) {
                allParticipants = allParticipants + ChatColor.GREEN+ participant.getName() + ChatColor.GRAY+ ", ";
            }

            player.sendMessage(Constants.TAG, line1);
            player.sendMessage(Constants.TAG, line2);
            player.sendMessage(allParticipants);
        } else {
            player.sendMessage(Constants.TAG, line1);
            player.sendMessage(Constants.TAG, new ComponentBuilder("No hay participanes en la party.").color(ChatColor.RED).create()[0]);
        }
    }
}
