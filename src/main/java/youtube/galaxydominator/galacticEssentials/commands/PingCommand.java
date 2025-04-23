package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PingCommand extends BaseCommand {

	MiniMessage mm = MiniMessage.miniMessage();

	@Default

	public void onPing(CommandSender sender,String[] args){
		if (!(sender instanceof Player)) {
			sender.sendMessage(mm.deserialize("<red>Only players can use this command."));
		}

		Player player = (Player) sender;

		if (!player.hasPermission("galactic-essentials.ping")) {
			player.sendMessage(mm.deserialize("<red>You don't have permission to use this command."));
		}

		if (args.length == 0){
			formatPing(player,player);
		}

		if (args.length == 1){
			if (!player.hasPermission("galactic-essentials.ping.others")) {
				player.sendMessage(mm.deserialize("<red>You don't have permission to check others' ping."));
			}
			Player targetPlayer = Bukkit.getPlayerExact(args[0]);
			if (targetPlayer == null){
				player.sendMessage(mm.deserialize("<red>That player is not online."));

			}
			formatPing(player,targetPlayer);
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
			if (!player.hasPermission("galactic-essentials.ping.all")) {
				player.sendMessage(mm.deserialize("<red>You don't have permission to check all players' ping."));
			}

			List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
			onlinePlayers.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));

			player.sendMessage(mm.deserialize("<gold>Ping list of online players:"));

			for (Player online : onlinePlayers) {
				formatPing(player, online);
			}
		}

	}



	private void formatPing(Player viewer, Player target){
		 int ping = target.getPing();
		String status;

		if (ping <= 49) {
			status = "<green>Excellent";
		} else if (ping <= 99) {
			status = "<dark_green>Good";
		} else if (ping <= 199) {
			status = "<yellow>Normal";
		} else if (ping <= 299) {
			status = "<gold>Medium";
		} else if (ping <= 499) {
			status = "<red>High";
		} else {
			status = "<dark_red><bold>Very High";
		}

		viewer.sendMessage(mm.deserialize("<gray>" + target.getName() + "'s ping: <white>" + ping + "ms</white> - " + status));
	}

}
