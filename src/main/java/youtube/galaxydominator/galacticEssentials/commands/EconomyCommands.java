package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.managers.GalacticPlayerManager;
import youtube.galaxydominator.galacticEssentials.player.GalacticPlayer;

@CommandAlias("eco")
@CommandPermission("galactic.essentials.eco")
public class EconomyCommands extends BaseCommand {

	private final MiniMessage mm = MiniMessage.miniMessage();

	@Default
	public void onEcoHelp(CommandSender sender) {
		sender.sendMessage(mm.deserialize("<aqua><bold>Economy Commands:</bold></aqua>"));
		sender.sendMessage(mm.deserialize("<gray>/eco set <player> <amount></gray>"));
		sender.sendMessage(mm.deserialize("<gray>/eco give <player> <amount></gray>"));
		sender.sendMessage(mm.deserialize("<gray>/eco reset <player></gray>"));
	}

	@Subcommand("set")
	@CommandCompletion("@players @nothing")
	public void onSet(CommandSender sender, String target, String amountStr) {
		Player targetPlayer = Bukkit.getPlayerExact(target);
		if (targetPlayer == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		try {
			double amount = Double.parseDouble(amountStr);
			GalacticPlayer gPlayer = GalacticPlayerManager.getInstance().get(targetPlayer.getUniqueId());
			gPlayer.setBalance(amount);
			GalacticPlayerManager.getInstance().save(gPlayer);
			sender.sendMessage(mm.deserialize("<green>Set " + target + "'s balance to $" + amount));
		} catch (NumberFormatException e) {
			sender.sendMessage(mm.deserialize("<red>Invalid amount."));
		}
	}

	@Subcommand("give")
	@CommandCompletion("@players @nothing")
	public void onGive(CommandSender sender, String target, String amountStr) {
		Player targetPlayer = Bukkit.getPlayerExact(target);
		if (targetPlayer == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		try {
			double amount = Double.parseDouble(amountStr);
			GalacticPlayer gPlayer = GalacticPlayerManager.getInstance().get(targetPlayer.getUniqueId());
			gPlayer.setBalance(gPlayer.getBalance() + amount);
			GalacticPlayerManager.getInstance().save(gPlayer);
			sender.sendMessage(mm.deserialize("<green>Gave $" + amount + " to " + target));
		} catch (NumberFormatException e) {
			sender.sendMessage(mm.deserialize("<red>Invalid amount."));
		}
	}

	@Subcommand("reset")
	@CommandCompletion("@players")
	public void onReset(CommandSender sender, String target) {
		Player targetPlayer = Bukkit.getPlayerExact(target);
		if (targetPlayer == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		GalacticPlayer gPlayer = GalacticPlayerManager.getInstance().get(targetPlayer.getUniqueId());
		gPlayer.setBalance(0);
		GalacticPlayerManager.getInstance().save(gPlayer);
		sender.sendMessage(mm.deserialize("<green>Reset " + target + "'s balance."));
	}
}
