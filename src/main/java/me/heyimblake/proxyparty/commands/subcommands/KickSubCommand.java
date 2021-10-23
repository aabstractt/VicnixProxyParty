package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartySetting;
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

@PartyAnnotationCommand(name = "kick", syntax = "/party kick <Jugador>", description = "Kickear a un jugador de la party", requiresArgumentCompletion = true, leaderExclusive = true)
public class KickSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (CommandConditions.checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party.getLeader().equals(targetUniqueId.toString()) || player.getUniqueId().equals(targetUniqueId)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No puedes kickearte a ti mismo o al lider de la party.").color(ChatColor.RED).create()[0]);

            return;
        }

        if (!party.getMembers().contains(targetUniqueId.toString())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador no esta en tu party!").color(ChatColor.RED).create()[0]);

            return;
        }

        RedisProvider.getInstance().removePartyMember(party.getUniqueId(), targetUniqueId);

        party.sendPartyMessage("PLAYER_LEAVE%" + targetUniqueId);
        RedisProvider.getInstance().sendPlayerMessage(targetUniqueId, "PARTY_KICK%" + player.getUniqueId().toString());

        player.sendMessage(ProxyParty.getInstance().translatePrefix(targetUniqueId) + ChatColor.YELLOW + " ha sido removido de tu party.");

        if ((party.getMembers().size() - 1) <= 0) {
            party.disband("PARTY_DISBAND_PLAYERS");
        }
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