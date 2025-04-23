package youtube.galaxydominator.galacticEssentials.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;

import java.util.*;

public class ChatListener implements Listener {

	private final MiniMessage mm = MiniMessage.miniMessage();
	private final Set<UUID> muted = new HashSet<>();
	private final Map<UUID, Integer> warnings = new HashMap<>();
	private final GalacticEssentials plugin;

	public ChatListener(GalacticEssentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();

		// Bypass permission skips filtering
		if (player.hasPermission("galactic-essentials.bypass")) return;

		// Cancel if muted
		if (muted.contains(uuid)) {
			e.setCancelled(true);
			player.sendMessage(mm.deserialize("<red>You are muted."));
			return;
		}

		String msg = e.getMessage().toLowerCase();
		List<String> badwords = plugin.getConfig().getStringList("word-filter");

		for (String word : badwords) {
			if (msg.contains(word.toLowerCase())) {
				int currentWarns = warnings.getOrDefault(uuid, 0);

				if (currentWarns < 2) {
					warnings.put(uuid, currentWarns + 1);
					player.sendMessage(mm.deserialize("<yellow>Warning → Your message contains restricted words."));
				} else {
					muted.add(uuid);
					player.sendMessage(mm.deserialize("<red>You have been muted for repeated violations."));

					// Notify staff
					for (Player online : plugin.getServer().getOnlinePlayers()) {
						if (online.hasPermission("galactic-essentials.notify")) {
							online.sendMessage(mm.deserialize("<gray>[Mod] <red>" + player.getName() + " has been auto-muted."));
						}
					}
				}

				e.setCancelled(true); // Cancel the bad message
				break;
			}
		}
	}
}
