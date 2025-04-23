package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("heal")
@CommandPermission("galactic-essentials.heal")
@Description("Heals yourself or another player")
public class HealCommand extends BaseCommand {

	private final MiniMessage mm = MiniMessage.miniMessage();

	@Default
	public void healSelf(Player player) {
		player.setHealth(20.0);
		player.sendMessage(mm.deserialize("<green>You have been healed."));
	}

	@Subcommand("")
	@CommandCompletion("@players")
	public void healOther(Player sender, @Name("target") Player target) {
		target.setHealth(20.0);
		target.sendMessage(mm.deserialize("<green>You have been healed by <yellow>" + sender.getName() + "</yellow>."));
		sender.sendMessage(mm.deserialize("<green>" + target.getName() + " has been healed."));
	}
}
