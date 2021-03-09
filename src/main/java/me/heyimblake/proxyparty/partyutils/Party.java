package me.heyimblake.proxyparty.partyutils;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.*;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class Party {

    private ProxiedPlayer leader;
    private final Set<ProxiedPlayer> participants;
    private Set<ProxiedPlayer> invited;
    private boolean partyPublic = false;

    /**
     * Creates a party with a leader and no participants.
     *
     * @param leader the party leader
     */
    protected Party(ProxiedPlayer leader) {
        this(leader, new HashSet<>());
    }

    /**
     * Creates a party with a leader and a set of participants.
     *
     * @param leader       the party leader
     * @param participants the set of participants
     */
    protected Party(ProxiedPlayer leader, Set<ProxiedPlayer> participants) {
        this.leader = leader;

        this.participants = participants;

        this.invited = new HashSet<>();

        this.getAllParticipants().forEach(participant -> {
            PartyRole.setRoleOf(participant, participant.getUniqueId().equals(leader.getUniqueId()) ? PartyRole.LEADER : PartyRole.PARTICIPANT);

            PartyManager.getInstance().getPlayerPartyMap().put(participant, this);
        });

        PartyManager.getInstance().getActiveParties().add(this);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyCreateEvent(this));
    }

    /**
     * Gets the leader of the instance of the party.
     *
     * @return the party leader.
     */
    public ProxiedPlayer getLeader() {
        return this.leader;
    }


    public Integer getMax() {
        int maxPerParty = 5;

        for (int i = maxPerParty; i <= 30; i++) {
            if (this.leader.hasPermission("party.maxsize." + i)) {
                return i;
            }
        }

        return maxPerParty;
    }

    public void setLeader() throws Exception {
        List<String> names = this.getParticipantNames();

        Collections.sort(names);

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(names.get(0));

        if (player == null) {
            throw new Exception("Player not found");
        }

        setLeader(player);
    }

    /**
     * Replaces the party leader with another proxied player.
     *
     * @param player the new party leader
     */
    public void setLeader(ProxiedPlayer player) {
        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyPromoteEvent(this, player, this.leader));

        if (this.leader != null) {
            PartyRole.setRoleOf(this.leader, PartyRole.PARTICIPANT);

            this.participants.add(this.leader);
        }

        this.sendPartyMessage(new TextComponent(Constants.LINE));
        this.sendPartyMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&eLa party fue transferida a &6%s&e por &d%s", player.getName(), leader.getName()))));
        this.sendPartyMessage(new TextComponent(Constants.LINE));

        this.leader = player;

        PartyRole.setRoleOf(player, PartyRole.LEADER);

        this.invited = new HashSet<>();

        if (this.isParticipant(player)) {
            this.participants.remove(player);
        }

        PartyManager.getInstance().getPlayerPartyMap().remove(player);
        PartyManager.getInstance().getPlayerPartyMap().put(player, this);
    }

    /**
     * Gets the set of participants.
     *
     * @return the set of participants of the party
     */
    public Set<ProxiedPlayer> getParticipants() {
        return this.participants;
    }

    public Boolean isParticipant(ProxiedPlayer player) {
        return this.participants.contains(player);
    }

    public Set<ProxiedPlayer> getAllParticipants() {
        return new HashSet<ProxiedPlayer>() {{
            addAll(participants);
            add(getLeader());
        }};
    }

    public List<String> getParticipantNames() {
        List<String> names = new ArrayList<>();

        for (ProxiedPlayer player : this.participants) {
            names.add(player.getName());
        }

        return names;
    }

    /**
     * Gets the currently invited set of players.
     *
     * @return set of invited players
     */
    public Set<ProxiedPlayer> getInvited() {
        return this.invited;
    }

    /**
     * Removes a player from the current party instance.
     *
     * @param player the player to be removed
     */
    public void removeParticipant(ProxiedPlayer player) {
        this.participants.remove(player);

        PartyManager.getInstance().getPlayerPartyMap().remove(player);
        PartyRole.removeRoleFrom(player);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyPlayerQuitEvent(this, player));
    }

    /**
     * Adds a player to the current party instance.
     *
     * @param player the player to be added
     */
    public void addParticipant(ProxiedPlayer player) {
        this.participants.add(player);

        PartyManager.getInstance().getPlayerPartyMap().put(player, this);
        PartyRole.setRoleOf(player, PartyRole.PARTICIPANT);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyPlayerJoinEvent(this, player));
    }

    /**
     * Invites a player to the current party instance.
     *
     * @param player the player to be invited
     */
    public void invitePlayer(ProxiedPlayer player) {
        this.invited.add(player);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartySendInviteEvent(this, player));
    }

    /**
     * Removes an invitation from a player invited to the current party instance.
     *
     * @param player the player to retract the invitation from
     */
    public void retractInvite(ProxiedPlayer player) {
        this.invited.remove(player);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyRetractInviteEvent(this, player));
    }

    /**
     * Send all party participants to a specified server.
     *
     * @param serverInfo the server to send the participants to
     */
    public void warpParticipants(ServerInfo serverInfo) {
        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyWarpEvent(this));

        this.participants.forEach(participant -> participant.connect(serverInfo));
    }

    /**
     * Sends a chat message from a player to the party chat.
     *
     * @param player the player sending the message
     * @param string the message to be sent
     */
    public void sendMessage(ProxiedPlayer player, String string) {
        TextComponent message = new TextComponent(player.getName() + ": ");

        message.setColor(ChatColor.YELLOW);

        getAllParticipants().forEach(participant -> participant.sendMessage(Constants.TAG, message, new TextComponent(string)));
    }

    /**
     * Sends a message with a specified chat color to all participants and leader of the current party instance.
     *
     * @param string    the message to be sent
     * @param chatColor the color of the message
     */
    public void sendMessage(String string, ChatColor chatColor) {
        TextComponent message = new TextComponent(string);

        message.setColor(chatColor);

        getAllParticipants().forEach(participant -> participant.sendMessage(Constants.TAG, message));
    }

    @SuppressWarnings("deprecation")
    public void sendPartyMessage(String string) {
        getAllParticipants().forEach(proxiedPlayer -> proxiedPlayer.sendMessage(string));
    }

    public void sendPartyMessage(TextComponent message) {
        getAllParticipants().forEach(proxiedPlayer -> proxiedPlayer.sendMessage(message));
    }

    /**
     * Disbands the current party instance.
     */
    public void disband() {
        this.disband(ChatColor.translateAlternateColorCodes('&', String.format("&d%s &eha borrado la party!", this.leader.getName())));
    }

    public void disband(String message) {
        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyDisbandEvent(this));

        this.sendPartyMessage(new TextComponent(Constants.LINE));
        this.sendPartyMessage(new TextComponent(message));
        this.sendPartyMessage(new TextComponent(Constants.LINE));

        this.getAllParticipants().forEach(participant -> PartyManager.getInstance().getPlayerPartyMap().remove(participant));
        this.getAllParticipants().forEach(PartyRole::removeRoleFrom);

        PartyManager.getInstance().getActiveParties().remove(this);
    }

    public String parseMembers() {
        String members = "";

        if (participants.isEmpty()) return members;

        for (ProxiedPlayer proxiedPlayer : this.participants) {
            members += proxiedPlayer.getName() + ",";
        }

        return members.substring(0, members.length() - 1);
    }

    public Boolean isPartyPublic() {
        return this.partyPublic;
    }

    public void setPartyPublic(Boolean partyPublic) {
        this.partyPublic = partyPublic;
    }
}