package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.events.PartyAcceptInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "entrar",
        syntax = "/party entrar <Jugador>",
        description = "Entrar a una party si es publica.",
        requiresArgumentCompletion = true,
        mustBeInParty = false
)
public class JoinSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (CommandConditions.checkTargetOnline(target, player)) return;

        if (CommandConditions.blockIfHasParty(player)) return;

        Party party = PartyManager.getInstance().getPartyOf(target);

        if (party == null || !party.getLeader().getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(
                    "No puedes acceder a esta party no existe o tú la creaste.")
                    .color(ChatColor.RED).create()[0]);
            return;
        }

        if (party.getParticipants().size() >= party.getMax()) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(
                    "La party a la que te intentas unir esta totalmente llena!")
                    .color(ChatColor.RED).bold(true).create()[0]);
            return;
        }

        if (!party.isPartyPublic()) {
            player.sendMessage(ChatColor.RED + "¡Está party no esta publica!");
            return;
        }

        party.addParticipant(player);

        if (party.getInvited().contains(player)) {
            party.getInvited().remove(player);
        }

        player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("Has ingresado a la %s's Party!!", target.getName())).color(ChatColor.GREEN).create()[0]);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyAcceptInviteEvent(party, player));
    }
}