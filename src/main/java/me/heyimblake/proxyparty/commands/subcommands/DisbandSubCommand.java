package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "disband",
        syntax = "/party disband",
        description = "Deshacer tu party!.",
        requiresArgumentCompletion = false,
        leaderExclusive = true
)
public class DisbandSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No estas en una party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if(!party.getLeader().getName().equalsIgnoreCase(player.getName())){
            player.sendMessage(ChatColor.RED+"No puedes eliminar una party sin ser lider.");
            return;
        }

        party.disband();

        player.sendMessage(Constants.TAG, new ComponentBuilder("Has eliminado la party.").color(ChatColor.YELLOW).create()[0]);
    }
}
