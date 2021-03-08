package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyCreator;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2017 heyimblake
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author heyimblake
 * @since 10/21/2016
 */
@PartyAnnotationCommand(
        name = "invite",
        syntax = "/party invite <Player>",
        description = "Invitar un jugador a tu party!",
        requiresArgumentCompletion = true,
        leaderExclusive = true,
        mustBeInParty = false)
public class InviteSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyParty.getInstance().getProxy().getPlayer(args[0]);

        /*if (target == null || target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No se puedes invitar este jugador a la party!").color(ChatColor.RED).create()[0]);

            return;
        }*/

        if(target.getServer().getInfo().getName().contains("Auth")){
            player.sendMessage(ChatColor.RED+"El jugador no ha iniciado sesión no puedes invitarlo.");

            return;
        }

        Party party = !PartyManager.getInstance().hasParty(player) ? new PartyCreator().setLeader(player).create() : PartyManager.getInstance().getPartyOf(player);

        if (party.getInvited().contains(target)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador ya fue invitado a tu party!!").color(ChatColor.RED).create()[0]);
            return;
        }

        if (PartySetting.PARTY_INVITE_RECIEVE_TOGGLE_OFF.getPlayers().contains(target)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador tiene desactivadas la invitaciones a una party!.").color(ChatColor.RED).create()[0]);
            return;
        }

        party.invitePlayer(target);

        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
        party.sendPartyMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&d%s &ainvito a &6%s&a a la party! Tiene &c%s&a segundos para &aaceptar.", player.getName(), target.getName(), "60"))));
        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
    }

    @Override
    public List<String> getComplete(String[] args) {
        List<String> complete = new ArrayList<>();

        String name = args[0];

        int lastSpaceIndex = name.lastIndexOf(' ');

        if (lastSpaceIndex >= 0) {
            name = name.substring(lastSpaceIndex + 1);
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.getName().toLowerCase().startsWith(name)) {
                continue;
            }

            if (complete.contains(player.getName())) continue;

            complete.add(player.getName());
        }

        Collections.sort(complete);

        return complete;
    }
}