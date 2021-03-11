package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.events.PartyWarpEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "warp",
        syntax = "/party warp",
        description = "Envia todos los jugadores a tu servidor..",
        requiresArgumentCompletion = false,
        leaderExclusive = true
)
public class WarpSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args)  {
        Party party = PartyManager.getInstance().getPartyOf(player);

        party.warpParticipants(player.getServer().getInfo());

        ProxyServer.getInstance().getPluginManager().callEvent(new PartyWarpEvent(party));

        party.sendMessage("El lider de la party los ha movido a este servidor!", ChatColor.AQUA);
    }
}
