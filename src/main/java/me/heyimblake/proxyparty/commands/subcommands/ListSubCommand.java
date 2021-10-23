package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PartyAnnotationCommand(
        name = "lista",
        syntax = "/party lista",
        description = "Miembros de la party.",
        requiresArgumentCompletion = false
)
public class ListSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        TextComponent line1 = new TextComponent("Lider de la Party: ");

        line1.setColor(ChatColor.YELLOW);
        line1.setBold(true);
        line1.addExtra(new TextComponent(ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(UUID.fromString(party.getLeader()), true)));

        if (party.getMembers().size() > 1) {
            TextComponent line2 = new TextComponent("Miembros de la Party");

            line2.setColor(ChatColor.AQUA);

            TextComponent count = new TextComponent(" (" + party.getMembers().size() + "/" + party.getMaxMembers() + ")");

            count.setColor(ChatColor.AQUA);
            line2.addExtra(count);

            List<String> membersString = new ArrayList<>();

            for (String memberId : party.getMembers()) {
                membersString.add(ProxyParty.getInstance().translatePrefix(UUID.fromString(memberId)));
            }

            player.sendMessage(line1);
            player.sendMessage(line2);
            player.sendMessage(String.join(ChatColor.GRAY + ", " + ChatColor.GREEN, membersString));
        } else {
            player.sendMessage(Constants.TAG, line1);
            player.sendMessage(Constants.TAG, new ComponentBuilder("No hay participanes en la party.").color(ChatColor.RED).create()[0]);
        }
    }
}