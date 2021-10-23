package me.heyimblake.proxyparty.commands.subcommands;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.events.PartySendInviteEvent;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@PartyAnnotationCommand(
        name = "invite",
        syntax = "/party invite <Player>",
        description = "Invitar un jugador a tu party!",
        requiresArgumentCompletion = true,
        leaderExclusive = true,
        mustBeInParty = false)
public class InviteSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
            UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

            if (CommandConditions.checkTargetOnline(targetUniqueId, player)) {
                return;
            }

            ServerInfo serverInfo = RedisBungee.getApi().getServerFor(targetUniqueId);

            if (/*player.getName().equalsIgnoreCase(args[0]) || */serverInfo == null || serverInfo.getName().contains("Auth")) {
                player.sendMessage(Constants.TAG, new ComponentBuilder("No se puedes invitar este jugador a la party!").color(ChatColor.RED).create()[0]);

                return;
            }

            RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

            if (party != null) {
                if (RedisProvider.getInstance().isAlreadyInvited(targetUniqueId, party.getUniqueId())) {
                    player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador ya fue invitado a tu party!!").color(ChatColor.RED).create()[0]);

                    return;
                }

                if (party.getMaxMembers() != -1 && party.getMembers().size() >= party.getMaxMembers()) {
                    player.sendMessage(new ComponentBuilder("Tu party esta totalmente llena, compra un rango mas superior en").color(ChatColor.RED).append("\n tienda.vincix.net ").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://tienda.vicnix.net")).append("para tener mas slots de party!").color(ChatColor.RED).create());

                    return;
                }
            } else {
                party = RedisProvider.getInstance().createParty(player.getUniqueId());
            }

            RedisProvider.getInstance().addPartyInvite(party.getUniqueId(), targetUniqueId);

            ProxyServer.getInstance().getPluginManager().callEvent(new PartySendInviteEvent(party.getUniqueId(), targetUniqueId));

            party.sendPartyMessage("PLAYER_INVITED%" + targetUniqueId);
        });
    }

    @Override
    public List<String> loadComplete(ProxiedPlayer player, String[] args) {
        List<String> complete = new ArrayList<>();

        String name = args[0];

        int lastSpaceIndex = name.lastIndexOf(' ');

        if (lastSpaceIndex >= 0) {
            name = name.substring(lastSpaceIndex + 1);
        }

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
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