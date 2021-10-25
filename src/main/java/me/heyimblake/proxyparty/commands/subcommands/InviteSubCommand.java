package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@PartyAnnotationCommand(name = "invite", syntax = "/party invite <Player>", description = "Invitar un jugador a tu party!", requiresArgumentCompletion = true, leaderExclusive = true, mustBeInParty = false)
public class InviteSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        String serverName = ProxyParty.getRedisBungee().getDataManager().getServer(targetUniqueId);

        if (player.getName().equalsIgnoreCase(args[0]) || serverName == null || serverName.contains("Auth")) {
            player.sendMessage(new ComponentBuilder("No se puede invitar este jugador a la party!").color(ChatColor.RED).create()[0]);

            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party != null) {
            if (RedisProvider.getInstance().isAlreadyInvited(targetUniqueId, party.getUniqueId())) {
                player.sendMessage(new ComponentBuilder("Este jugador ya fue invitado a tu party!!").color(ChatColor.RED).create()[0]);

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

        party.sendPartyMessage("PLAYER_INVITED%" + targetUniqueId);

        RedisProvider.getInstance().sendPlayerMessage(targetUniqueId, "PARTY_INVITATION_RECEIVED%" + party.getUniqueId());
    }
}