package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.managers.GalacticPlayerManager;
import youtube.galaxydominator.galacticEssentials.player.GalacticPlayer;

@CommandAlias("balance|bal")
public class BalanceCommand extends BaseCommand {

	private final MiniMessage mm = MiniMessage.miniMessage();

	@Default
	public void onBalance(CommandSender sender, String[] args) {

		if (args.length == 0) {
			// /balance - show your own balance
			if (sender instanceof Player player) {
				GalacticPlayer galacticPlayer = GalacticPlayerManager.getInstance().get(player.getUniqueId());
				player.sendMessage(mm.deserialize("<white>Your current balance is: <green>$" + galacticPlayer.getBalance() + "</green>"));
			} else {
				sender.sendMessage(mm.deserialize("<red>You must specify a player when using this from console."));
			}
			return;
		}

		// /balance <player>
		Player target = Bukkit.getPlayerExact(args[0]);
		if (target == null) {
			sender.sendMessage(mm.deserialize("<red>Player '" + args[0] + "' is not online."));
			return;
		}

		GalacticPlayer targetData = GalacticPlayerManager.getInstance().get(target.getUniqueId());
		sender.sendMessage(mm.deserialize("<white>" + target.getName() + "'s balance is: <green>$" + targetData.getBalance() + "</green>"));
	}
}
