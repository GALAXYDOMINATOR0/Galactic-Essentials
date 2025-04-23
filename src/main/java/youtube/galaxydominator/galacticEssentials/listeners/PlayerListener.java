package youtube.galaxydominator.galacticEssentials.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import youtube.galaxydominator.galacticEssentials.managers.GalacticPlayerManager;
import youtube.galaxydominator.galacticEssentials.player.GalacticPlayer;

import java.util.*;

public class PlayerListener implements Listener {

	private final Map<UUID, Long> lastActivity = new HashMap<>();
	private final Set<UUID> afkPlayers = new HashSet<>();
	private final long AFK_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

	private final JavaPlugin plugin;
	private final MiniMessage mm = MiniMessage.miniMessage();

	public PlayerListener(JavaPlugin plugin) {
		this.plugin = plugin;
		startAfkCheckTimer();
	}

	// This starts a repeating task that checks if a player should be marked AFK or not
	private void startAfkCheckTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();

				for (Player player : Bukkit.getOnlinePlayers()) {
					UUID uuid = player.getUniqueId();
					long last = lastActivity.getOrDefault(uuid, now);

					if (!afkPlayers.contains(uuid) && now - last >= AFK_TIME) {
						afkPlayers.add(uuid);
						Bukkit.broadcast(Component.text(player.getName() + " is now AFK."));
					} else if (afkPlayers.contains(uuid) && now - last < AFK_TIME) {
						afkPlayers.remove(uuid);
						Bukkit.broadcast(Component.text(player.getName() + " is no longer AFK."));
					}
				}
			}
		}.runTaskTimer(plugin, 20L, 20L); // Runs every second
	}

	// Any player movement updates their last activity time
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		updateActivity(event.getPlayer());
	}

	// Player chats = activity
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		updateActivity(event.getPlayer());
	}




	// Player interacts (like clicking) = activity
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		updateActivity(event.getPlayer());
	}

	@EventHandler
	public void onPreJoin(AsyncPlayerPreLoginEvent e) {
		UUID uuid = e.getUniqueId();

		// Load from DB (off-thread safe)
		GalacticPlayer player = GalacticPlayer.load(uuid);

		// If player doesn't exist in DB, create and save
		if (player == null) {
			player = new GalacticPlayer(uuid);
			player.save(); // Optional: first-time save
		}

		// Cache the player for use on main thread
		GalacticPlayerManager.getInstance().cache(uuid, player);
	}


	// Player joins = set current time as last activity
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Update activity for AFK detection
		updateActivity(player);

		// Cancel default join message
		event.setJoinMessage(null);

		// Custom join message from config
		List<String> msg = plugin.getConfig().getStringList("join-message.messages");
		if (plugin.getConfig().getBoolean("join-message.enabled")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				for (String line : msg) {
					online.sendMessage(mm.deserialize(line.replace("%player%", player.getName())));
				}
			}
		}
	}

	// Clean up when a player leaves
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();

		// Remove from activity tracking
		lastActivity.remove(uuid);
		afkPlayers.remove(uuid);

		// Save to DB
		GalacticPlayerManager.getInstance().save(event.getPlayer());

		// Remove from cache
		GalacticPlayerManager.getInstance().remove(uuid);
	}


	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if (!(e.getEntity() instanceof Player)) return;

		Player victim = e.getEntity();
		Player killer = victim.getKiller();

		if (killer != null && killer != victim) {
			// Update killer stats
			GalacticPlayer killerData = GalacticPlayerManager.getInstance().get(killer.getUniqueId());
			if (killerData != null) {
				killerData.setKills(killerData.getKills() + 1);
				GalacticPlayerManager.getInstance().save(killerData);
			}
		}

		// Update victim stats
		GalacticPlayer victimData = GalacticPlayerManager.getInstance().get(victim.getUniqueId());
		if (victimData != null) {
			victimData.setDeaths(victimData.getDeaths() + 1);
			GalacticPlayerManager.getInstance().save(victimData);
		}
	}

	// Updates the last activity timestamp
	private void updateActivity(Player player) {
		UUID uuid = player.getUniqueId();
		lastActivity.put(uuid, System.currentTimeMillis());

		// If they were AFK and now they're active, mark them not AFK
		if (afkPlayers.contains(uuid)) {
			afkPlayers.remove(uuid);
			Bukkit.broadcast(Component.text(player.getName() + " is no longer AFK."));
		}
	}
}
