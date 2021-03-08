package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartyRole;
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
            party.disband();

            return;
        }

        if (PartyRole.getRoleOf(player) == PartyRole.LEADER) {
            party.setLeader();
        }

        party.removeParticipant(player);

        PartyManager.getInstance().getActiveParties().forEach(party1 -> party1.getInvited().remove(player));
    }
}
