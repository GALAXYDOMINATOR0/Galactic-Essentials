package youtube.galaxydominator.galacticEssentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;
import youtube.galaxydominator.galacticEssentials.managers.WarpManager;

@CommandAlias("warp|warps")
public class WarpCommands extends BaseCommand {

	private final GalacticEssentials plugin;
	private final WarpManager warpManager;

	public WarpCommands(GalacticEssentials plugin) {
		this.plugin = plugin;
		this.warpManager = plugin.getWarpManager();
	}

	@CommandAlias("setwarp")
	@CommandCompletion("@nothing")
	@CommandPermission("galactic-essentials.warp.set")
	@Description("Sets a warp at your current location.")
	public void onSetWarp(Player player, String warpName) {
		warpManager.setWarp(warpName, player.getLocation());
		player.sendMessage("§aWarp §e" + warpName + " §ahas been set.");
	}

	@Default
	@CommandCompletion("@warps")
	@Description("Teleports to a warp.")
	public void onWarp(Player player, String warpName) {
		warpManager.teleport(warpName, player);
	}


	@CommandAlias("delwarp")
	@CommandCompletion("@warps")
	@CommandPermission("galactic-essentials.warp.delete")
	@Description("Deletes a warp.")
	public void onDeleteWarp(Player player, String warpName) {
		if (warpManager.deleteWarp(warpName)) {
			player.sendMessage("§cWarp §e" + warpName + " §chas been deleted.");
		} else {
			player.sendMessage("§cWarp §e" + warpName + " §cdoes not exist.");
		}
	}


}
