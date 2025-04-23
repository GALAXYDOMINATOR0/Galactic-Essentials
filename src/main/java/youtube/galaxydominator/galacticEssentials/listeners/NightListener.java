package youtube.galaxydominator.galacticEssentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.scheduler.BukkitRunnable;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;

public class NightListener implements Listener {

	private final GalacticEssentials plugin;

	public NightListener(GalacticEssentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

		World world = event.getPlayer().getWorld();

		if (!world.getName().equalsIgnoreCase("world")) return; // only in overworld

		Bukkit.broadcastMessage("§6" + event.getPlayer().getName() + " §ais sleeping... Skipping night!");

		new BukkitRunnable() {
			@Override
			public void run() {
				world.setTime(0); // reset to day
//				world.setStorm(false); // clear weather
//				world.setThundering(false);
				Bukkit.broadcastMessage("§b☀ The night has been skipped!");
			}
		}.runTaskLater(plugin, 60L); // wait 3 seconds (60 ticks)
	}
}


