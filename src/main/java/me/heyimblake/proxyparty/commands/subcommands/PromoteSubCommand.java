package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@PartyAnnotationCommand(
        name = "promote",
        syntax = "/party promote <Jugador>",
        description = "Transferir el propietario de la party a un jugador de la party",
        requiresArgumentCompletion = true,
        leaderExclusive = true
)
public class PromoteSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (CommandConditions.checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (!party.getMembers().contains(targetUniqueId.toString())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador no esta en tu party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if (targetUniqueId.equals(player.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No puedes transferir la party a ti mismo.").color(ChatColor.RED).create()[0]);

            return;
        }

        party.setLeader(targetUniqueId);
    }

    @Override
    public List<String> loadComplete(ProxiedPlayer player, String[] args) {
        List<String> complete = new ArrayList<>();

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) {
            return complete;
        }

        String name = args[0];

        int lastSpaceIndex = name.lastIndexOf(' ');

        if (lastSpaceIndex >= 0) {
            name = name.substring(lastSpaceIndex + 1);
        }

        for (ProxiedPlayer proxiedPlayer : party.getParticipants()) {
            if (!proxiedPlayer.getName().toLowerCase().startsWith(name.toLowerCase())) {
                continue;
            }

            if (complete.contains(proxiedPlayer.getName())) continue;

            complete.add(proxiedPlayer.getName());
        }

        Collections.sort(complete);

        return complete;
    }
}