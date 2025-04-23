package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;
import youtube.galaxydominator.galacticEssentials.teleport.TeleportManager;

@CommandAlias("tpa|tpaccept|tpdeny")
public class TeleportCommand extends BaseCommand {

	private final TeleportManager teleportManager;

	public TeleportCommand(GalacticEssentials plugin) {
		this.teleportManager = plugin.getTeleportManager();
	}

	@Subcommand("tpa")
	@Description("Send a teleport request to another player.")
	@CommandCompletion("@players")
	public void onTpa(Player sender, @Name("target") Player target) {
		if (sender.equals(target)) {
			sender.sendMessage("§cYou can't send a TPA request to yourself.");
			return;
		}

		teleportManager.sendRequest(sender, target);
	}

	@Subcommand("tpaccept")
	@Description("Accept a pending teleport request.")
	public void onTpAccept(Player player) {
		if (!teleportManager.hasRequest(player)) {
			player.sendMessage("§cYou have no teleport requests.");
			return;
		}

		teleportManager.acceptRequest(player);
	}

	@Subcommand("tpadeny")
	@Description("Deny a pending teleport request.")
	public void onTpDeny(Player player) {
		if (!teleportManager.hasRequest(player)) {
			player.sendMessage("§cYou have no teleport requests.");
			return;
		}

		teleportManager.denyRequest(player);
	}
}
