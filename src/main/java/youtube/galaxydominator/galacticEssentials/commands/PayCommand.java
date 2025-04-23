package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.managers.GalacticPlayerManager;
import youtube.galaxydominator.galacticEssentials.player.GalacticPlayer;

@CommandAlias("pay")
@CommandPermission("galactic-essentials.pay")
public class PayCommand extends BaseCommand {

	private final MiniMessage mm = MiniMessage.miniMessage();

	@Default
	@CommandCompletion("@players 100|500|1000")
	public void onPay(Player sender, @Name("target") Player target, @Name("amount") String amountStr) {
		if (target.equals(sender)) {
			sender.sendMessage(mm.deserialize("<red>You cannot pay yourself."));
			return;
		}

		GalacticPlayer galacticSender = GalacticPlayerManager.getInstance().get(sender.getUniqueId());
		GalacticPlayer galacticTarget = GalacticPlayerManager.getInstance().get(target.getUniqueId());

		if (galacticTarget == null) {
			sender.sendMessage(mm.deserialize("<red>That player is not registered."));
			return;
		}

		try {
			double amount = Double.parseDouble(amountStr);

			if (amount <= 0) {
				sender.sendMessage(mm.deserialize("<red>Amount must be positive."));
				return;
			}

			if (galacticSender.getBalance() < amount) {
				sender.sendMessage(mm.deserialize("<red>You do not have enough funds."));
				return;
			}

			galacticSender.setBalance(galacticSender.getBalance() - amount);
			galacticTarget.setBalance(galacticTarget.getBalance() + amount);

			galacticSender.save();
			galacticTarget.save();

			sender.sendMessage(mm.deserialize("<green>You sent <yellow>$" + amount + "</yellow> to <white>" + target.getName() + "</white>."));
			target.sendMessage(mm.deserialize("<green>You received <yellow>$" + amount + "</yellow> from <white>" + sender.getName() + "</white>."));

		} catch (NumberFormatException e) {
			sender.sendMessage(mm.deserialize("<red>Invalid amount."));
		} catch (Exception e) {
			sender.sendMessage(mm.deserialize("<red>An error occurred while processing payment."));
			e.printStackTrace(); // Log to console
		}
	}
}
