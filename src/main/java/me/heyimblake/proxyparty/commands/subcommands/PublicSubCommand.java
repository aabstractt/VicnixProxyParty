package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "publica",
        syntax = "/party publica",
        description = "Cambiar el estado de tú party a publica o privada",
        requiresArgumentCompletion = false,
        leaderExclusive = true
)
public class PublicSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) {
            player.sendMessage(ChatColor.RED + "Debes tener una party!");

            return;
        }

        if (!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Debes ser lider la party!");

            return;
        }

        party.setPartyPublic(!party.isPartyPublic());

        party.sendPartyMessage(new TextComponent(Constants.LINE));
        party.sendPartyMessage(ChatColor.GREEN + "La party ahora es " + ChatColor.GOLD + (party.isPartyPublic() ? "PUBLICA" : "PRIVADA"));
        party.sendPartyMessage(new TextComponent(Constants.LINE));
    }
}