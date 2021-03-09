package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartyRole;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "salir",
        syntax = "/party salir",
        description = "Salir de una party!",
        requiresArgumentCompletion = false
)
public class LeaveSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party.getAllParticipants().size() <= 2) {
            party.disband(ChatColor.RED + "La party ha sido borrada debido a la falta de jugadores");

            return;
        }

        try {
            if (PartyRole.getRoleOf(player) == PartyRole.LEADER) {
                party.setLeader();
            }

            party.removeParticipant(player);

            PartyManager.getInstance().getActiveParties().forEach(party1 -> party1.getInvited().remove(player));
        } catch (Exception e) {
            party.disband(ChatColor.RED + "La party ha sido borrada debido a que no se encontro un lider");
        }
    }
}
