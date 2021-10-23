package me.heyimblake.proxyparty.commands;

import me.heyimblake.proxyparty.commands.subcommands.*;
import me.heyimblake.proxyparty.partyutils.PartyRole;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.logging.Level;

public class PartyCommand extends Command implements TabExecutor {

    private final Map<String, PartySubCommand> commands = new HashMap<>();
    private final Map<String, String> commandsAlias = new HashMap<>();

    public PartyCommand() {
        super("party", null, "fiesta", "p");
        registerSubCommand(new InviteSubCommand());
        registerSubCommand(new AcceptSubCommand(), "accept");
        registerSubCommand(new AcceptSubCommand());
        registerSubCommand(new DenySubCommand());
        registerSubCommand(new DenySubCommand(), "deny");
        registerSubCommand(new FindSubCommand());
        registerSubCommand(new ListSubCommand());
        registerSubCommand(new ListSubCommand(), "list");
        registerSubCommand(new InvitedSubCommand());
        registerSubCommand(new RetractSubCommand());
        registerSubCommand(new KickSubCommand());
        registerSubCommand(new ChatSubCommand());
        registerSubCommand(new WarpSubCommand());
        registerSubCommand(new LeaveSubCommand());
        registerSubCommand(new LeaveSubCommand(), "leave");
        registerSubCommand(new JoinSubCommand());
        registerSubCommand(new JoinSubCommand(), "join");
        registerSubCommand(new PublicSubCommand());
        registerSubCommand(new PublicSubCommand(), "public");
        registerSubCommand(new DisbandSubCommand());
        registerSubCommand(new ToggleSubCommand());

        registerSubCommand(new PromoteSubCommand());
        registerSubCommand(new PromoteSubCommand(), "transfer");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Constants.TAG, new TextComponent("No puedes ejecutar comandos desde la consola."));

            return;
        }

        ProxiedPlayer player = ((ProxiedPlayer) sender);

        if (player.getServer().getInfo().getName().contains("Auth")) {
            player.sendMessage(ChatColor.RED + "No puedes ejecutar comandos en el auth");
            return;
        }

        if (args.length <= 0) {
            showHelpMessage(player);

            return;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

        PartySubCommand subCommand = this.getCommand(args[0]);

        if (subCommand == null) {
            showHelpMessage(player);

            return;
        }

        PartyAnnotationCommand annotations = subCommand.getAnnotations();

        if (annotations == null) return;

        if (PartyRole.getRoleOf(player) == PartyRole.PARTICIPANT && annotations.leaderExclusive()) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(
                    "Debes ser lider de la party para usar este comando").color(ChatColor.RED).create()[0]);

            return;
        }

        if (newArgs.length == 0 && annotations.requiresArgumentCompletion()) {
            player.sendMessage(Constants.TAG, new TextComponent("Uso: "),
                    new ComponentBuilder(annotations.syntax()).color(ChatColor.GREEN)
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/party "
                                    + args[0] + " "))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.YELLOW + "Click to prepare command.")}))
                            .create()[0]);
            return;
        }

        if (RedisProvider.getInstance().getParty(player.getUniqueId()) == null && annotations.mustBeInParty()) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Debes estar en una party para ejecutar este comando!").color(ChatColor.RED).create()[0]);

            return;
        }

        long start = System.nanoTime();

        subCommand.execute(player, newArgs);

        long elapsed = System.nanoTime() - start;

        if (elapsed > 250000000) {
            ProxyServer.getInstance().getLogger().log( Level.WARNING, "Event {0} took {1}ms to process!", new Object[]{subCommand.getAnnotations().name(), elapsed / 1000000});
        }
    }

    private void showHelpMessage(ProxiedPlayer player) {
        TextComponent topMSG = new TextComponent("Comandos de Party");

        topMSG.setColor(ChatColor.GOLD);
        topMSG.setBold(true);

        player.sendMessage(topMSG);

        TextComponent prepareMSG = new TextComponent("Click para ejecutar el comando.");

        prepareMSG.setColor(ChatColor.WHITE);
        prepareMSG.setItalic(true);

        for (PartySubCommand command : commands.values()) {
            PartyAnnotationCommand annotations = command.getAnnotations();

            if (annotations == null) continue;

            TextComponent pt1 = new TextComponent(annotations.syntax());
            pt1.setColor(ChatColor.YELLOW);
            pt1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{prepareMSG}));
            pt1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/party " + annotations.name() + " "));

            TextComponent pt2 = new TextComponent(" - ");
            pt2.setColor(ChatColor.DARK_GRAY);

            TextComponent pt3 = new TextComponent(annotations.description());
            pt3.setColor(ChatColor.WHITE);
            pt3.setItalic(true);

            player.sendMessage(pt1, pt2, pt3);
        }

        player.sendMessage(new TextComponent(" "));
    }

    private void registerSubCommand(PartySubCommand subCommand) {
        registerSubCommand(subCommand, null);
    }

    private void registerSubCommand(PartySubCommand subCommand, String name) {
        PartyAnnotationCommand annotation = subCommand.getAnnotations();

        if (annotation == null) return;

        if (name == null) {
            commands.put(annotation.name(), subCommand);
        } else {
            commandsAlias.put(name, annotation.name());
        }
    }

    private PartySubCommand getCommand(String name) {
        String alias = this.commandsAlias.get(name.toLowerCase());

        if (alias != null) name = alias;

        return commands.get(name.toLowerCase());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> complete = new ArrayList<>();

        if (!(commandSender instanceof ProxiedPlayer)) {
            return complete;
        }

        if (args.length == 0) {
            return complete;
        }

        if (args.length == 1) {
            String name = args[0];

            int lastSpaceIndex = name.lastIndexOf(' ');

            if (lastSpaceIndex >= 0) {
                name = name.substring(lastSpaceIndex + 1);
            }

            List<String> commands = new ArrayList<>(this.commands.keySet());

            commands.addAll(this.commandsAlias.keySet());

            for (String commandName : commands) {
                if (!commandName.toLowerCase().startsWith(name)) {
                    continue;
                }

                if (complete.contains(commandName)) continue;

                complete.add(commandName);
            }

            Collections.sort(complete);

            return complete;
        }

        PartySubCommand command = this.getCommand(args[0]);

        if (command == null) {
            return complete;
        }

        return command.loadComplete((ProxiedPlayer) commandSender, Arrays.copyOfRange(args, 1, args.length));
    }
}