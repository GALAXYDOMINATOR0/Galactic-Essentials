package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bson.Document;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;
import youtube.galaxydominator.galacticEssentials.logs.BankLog;
import youtube.galaxydominator.galacticEssentials.managers.GalacticPlayerManager;
import youtube.galaxydominator.galacticEssentials.player.GalacticPlayer;

@CommandAlias("bank")
public class BankCommand extends BaseCommand {

	private final MiniMessage mm = MiniMessage.miniMessage();
	private final GalacticEssentials plugin = GalacticEssentials.getPlugin(GalacticEssentials.class);

	@Default
	public void onCheck(Player player) {
		GalacticPlayer gPlayer = GalacticPlayerManager.getInstance().get(player.getUniqueId());
		player.sendMessage(mm.deserialize(plugin.getBankConfig().getString("messages.balance")
				.replace("%amount%", String.valueOf(gPlayer.getBankBalance()))));
	}

	@Subcommand("deposit")
	public void onDeposit(Player player, String amountStr) {
		double amount = parseAmount(player, amountStr);
		if (amount == -1) return;

		GalacticPlayer gPlayer = GalacticPlayerManager.getInstance().get(player.getUniqueId());

		if (gPlayer.getBalance() < amount) {
			player.sendMessage(mm.deserialize("<red>You don't have enough money to deposit."));
			return;
		}

		gPlayer.setBalance(gPlayer.getBalance() - amount);
		gPlayer.setBankBalance(gPlayer.getBankBalance() + amount);
		gPlayer.save();

		plugin.getBankLogManager().log(BankLog.createLog(
				gPlayer.getUuid(), gPlayer.getName(), "DEPOSIT", amount, player.getName(), false));

		player.sendMessage(mm.deserialize(plugin.getBankConfig().getString("messages.deposit-success")
				.replace("%amount%", String.valueOf(amount))));
	}

	@Subcommand("withdraw")
	public void onWithdraw(Player player, String amountStr) {
		double amount = parseAmount(player, amountStr);
		if (amount == -1) return;

		GalacticPlayer gPlayer = GalacticPlayerManager.getInstance().get(player.getUniqueId());

		if (gPlayer.getBankBalance() < amount) {
			player.sendMessage(mm.deserialize(plugin.getBankConfig().getString("messages.withdraw-fail")));
			return;
		}

		gPlayer.setBankBalance(gPlayer.getBankBalance() - amount);
		gPlayer.setBalance(gPlayer.getBalance() + amount);
		gPlayer.save();

		plugin.getBankLogManager().log(BankLog.createLog(
				gPlayer.getUuid(), gPlayer.getName(), "WITHDRAW", amount, player.getName(), false));

		player.sendMessage(mm.deserialize("<green>Withdrew $" + amount + " from your bank."));
	}

	@Subcommand("set")
	@CommandPermission("bank.admin")
	public void onSet(CommandSender sender, String targetName, String amountStr) {
		double amount = parseAmount(sender, amountStr);
		if (amount == -1) return;

		Player target = Bukkit.getPlayerExact(targetName);
		if (target == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		GalacticPlayer gp = GalacticPlayerManager.getInstance().get(target.getUniqueId());
		gp.setBankBalance(amount);
		gp.save();

		plugin.getBankLogManager().log(BankLog.createLog(
				gp.getUuid(), gp.getName(), "SET", amount, sender.getName(), true));

		sender.sendMessage(mm.deserialize("<green>Set " + target.getName() + "'s bank balance to $" + amount));
	}

	@Subcommand("give")
	@CommandPermission("bank.admin")
	public void onGive(CommandSender sender, String targetName, String amountStr) {
		double amount = parseAmount(sender, amountStr);
		if (amount == -1) return;

		Player target = Bukkit.getPlayerExact(targetName);
		if (target == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		GalacticPlayer gp = GalacticPlayerManager.getInstance().get(target.getUniqueId());
		gp.setBankBalance(gp.getBankBalance() + amount);
		gp.save();

		plugin.getBankLogManager().log(BankLog.createLog(
				gp.getUuid(), gp.getName(), "GIVE", amount, sender.getName(), true));

		sender.sendMessage(mm.deserialize("<green>Gave $" + amount + " to " + target.getName() + "'s bank."));
	}

	@Subcommand("take")
	@CommandPermission("bank.admin")
	public void onTake(CommandSender sender, String targetName, String amountStr) {
		double amount = parseAmount(sender, amountStr);
		if (amount == -1) return;

		Player target = Bukkit.getPlayerExact(targetName);
		if (target == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		GalacticPlayer gp = GalacticPlayerManager.getInstance().get(target.getUniqueId());
		double newBalance = Math.max(0, gp.getBankBalance() - amount);
		gp.setBankBalance(newBalance);
		gp.save();

		plugin.getBankLogManager().log(BankLog.createLog(
				gp.getUuid(), gp.getName(), "TAKE", amount, sender.getName(), true));

		sender.sendMessage(mm.deserialize("<green>Took $" + amount + " from " + target.getName() + "'s bank."));
	}

	@Subcommand("view")
	@CommandPermission("bank.admin")
	public void onView(CommandSender sender, String targetName) {
		Player target = Bukkit.getPlayerExact(targetName);
		if (target == null) {
			sender.sendMessage(mm.deserialize("<red>Player not found."));
			return;
		}

		GalacticPlayer gp = GalacticPlayerManager.getInstance().get(target.getUniqueId());
		sender.sendMessage(mm.deserialize("<gray>" + target.getName() + "'s bank balance: <green>$" + gp.getBankBalance()));
	}

	private double parseAmount(CommandSender sender, String input) {
		try {
			double amount = Double.parseDouble(input);
			if (amount <= 0) {
				sender.sendMessage(mm.deserialize("<red>Amount must be positive."));
				return -1;
			}
			return amount;
		} catch (NumberFormatException e) {
			sender.sendMessage(mm.deserialize("<red>Invalid number."));
			return -1;
		}
	}
}
