package youtube.galaxydominator.galacticEssentials.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpManager {

	private GalacticEssentials plugin;

	public WarpManager(GalacticEssentials plugin) {
		this.plugin = plugin;
	}

	public void setWarp(String warpName, Location location) {
		FileConfiguration config = plugin.getWarpsConfig(); // your custom config

		ConfigurationSection section = config.createSection("warps." + warpName);
		section.set("world", location.getWorld().getName());
		section.set("x", location.getX());
		section.set("y", location.getY());
		section.set("z", location.getZ());
		section.set("yaw", location.getYaw());
		section.set("pitch", location.getPitch());

		plugin.saveWarpsConfig(); // Your method to save the file
	}

	public void teleport(String warpName, Player player) {
		FileConfiguration config = plugin.getWarpsConfig(); // your custom config
		ConfigurationSection section = config.getConfigurationSection("warps." + warpName);

		if (section == null) {
			player.sendMessage("§cWarp '" + warpName + "' not found!");
			return;
		}

		World world = Bukkit.getWorld(section.getString("world"));
		if (world == null) {
			player.sendMessage("§cWorld '" + section.getString("world") + "' not found!");
			return;
		}

		double x = section.getDouble("x");
		double y = section.getDouble("y");
		double z = section.getDouble("z");
		float yaw = (float) section.getDouble("yaw");
		float pitch = (float) section.getDouble("pitch");

		Location location = new Location(world, x, y, z, yaw, pitch);
		player.teleport(location);
		player.sendMessage("§aTeleported to warp: §e" + warpName);
	}

	public boolean deleteWarp(String warpName) {
		FileConfiguration config = plugin.getWarpsConfig();
		String path = "warps." + warpName;
		if (config.contains(path)) {
			config.set(path, null);
			plugin.saveWarpsConfig();
			return true;
		}
		return false;
	}

	public List<String> getAllWarpNames() {
		FileConfiguration config = plugin.getWarpsConfig();
		ConfigurationSection warpsSection = config.getConfigurationSection("warps");
		if (warpsSection == null) return Collections.emptyList();
		return new ArrayList<>(warpsSection.getKeys(false));
	}



}
