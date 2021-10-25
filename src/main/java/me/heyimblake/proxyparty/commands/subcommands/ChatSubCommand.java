package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

@PartyAnnotationCommand(
        name = "chat",
        syntax = "/party chat (mensaje)",
        description = "Envia un mensaje a los miembros de la party.",
        requiresArgumentCompletion = false
)
public class ChatSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(new ComponentBuilder(!PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player)
                    ? "Todos los mensajes que se envien se enviaran a la party."
                    : "Los mensajes ya no se enviaran a la party!.")
                    .color(!PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player) ? ChatColor.GREEN : ChatColor.RED).create());

            if (PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player)) {
                PartySetting.PARTY_CHAT_TOGGLE_ON.disable(player);
            } else {
                PartySetting.PARTY_CHAT_TOGGLE_ON.enable(player);
            }

            return;
        }

        final String[] message = {""};

        Arrays.stream(args).forEach(string -> message[0] += string + " ");

        RedisProvider.getInstance().getParty(player.getUniqueId()).sendPartyMessage("PLAYER_CHAT%" + player.getUniqueId().toString() + "%" + message[0]);
    }
}