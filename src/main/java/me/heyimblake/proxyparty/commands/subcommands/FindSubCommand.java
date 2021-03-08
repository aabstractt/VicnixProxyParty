package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "find",
        syntax = "/party find <Jugador>",
        description = "Mira donde esta jugando el jugador de tu party.",
        requiresArgumentCompletion = true
)
public class FindSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (CommandConditions.checkTargetOnline(target, player)) return;

        if (PartyManager.getInstance().getPartyOf(target).getLeader().getUniqueId() != PartyManager.getInstance().getPartyOf(player).getLeader().getUniqueId()) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador no es miembro de tu party!").color(ChatColor.RED).create()[0]);
            return;
        }

        ServerInfo serverInfo = target.getServer().getInfo();

        player.sendMessage(ChatColor.AQUA+target.getName()+ ChatColor.GREEN+" esta jugando en "+ChatColor.AQUA+serverInfo.getName()+".");
    }
}
