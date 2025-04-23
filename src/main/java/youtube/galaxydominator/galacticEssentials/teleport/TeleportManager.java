package youtube.galaxydominator.galacticEssentials.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.GalacticEssentials;

import java.util.*;

public class TeleportManager {

	private final Map<UUID, TeleportRequest> requests = new HashMap<>();
	private final Map<UUID, Long> cooldowns = new HashMap<>();
	private final GalacticEssentials plugin;
	private final MiniMessage mm = MiniMessage.miniMessage();

	public TeleportManager(GalacticEssentials plugin) {
		this.plugin = plugin;
	}

	public void sendRequest(Player sender, Player target) {
		FileConfiguration config = plugin.getConfig();
		long cooldownTime = config.getLong("tpa.cooldown-seconds") * 1000L;

		if (cooldowns.containsKey(sender.getUniqueId())) {
			long last = cooldowns.get(sender.getUniqueId());
			if (System.currentTimeMillis() - last < cooldownTime) {
				sender.sendMessage(mm.deserialize(config.getString("tpa.messages.cooldown")));
				return;
			}
		}

		TeleportRequest request = new TeleportRequest(sender, target);
		requests.put(target.getUniqueId(), request);
		cooldowns.put(sender.getUniqueId(), System.currentTimeMillis());

		sender.sendMessage(mm.deserialize(config.getString("tpa.messages.request-sent")
				.replace("%target%", target.getName())));

		target.sendMessage(mm.deserialize(config.getString("tpa.messages.request-received")
				.replace("%sender%", sender.getName())));

		sendClickableMessage(target, sender);

		long timeout = config.getLong("tpa.timeout-seconds") * 20L;
		Bukkit.getScheduler().runTaskLater(plugin, () -> expireRequest(target.getUniqueId()), timeout);
	}

	public void acceptRequest(Player target) {
		TeleportRequest request = requests.remove(target.getUniqueId());
		if (request == null) return;

		Player sender = request.getSender();
		FileConfiguration config = plugin.getConfig();

		int delaySeconds = config.getInt("tpa.delay-seconds", 5);
		long delayTicks = delaySeconds * 20L;

		sender.sendMessage(mm.deserialize(config.getString("tpa.messages.teleport-delay")
				.replace("%seconds%", String.valueOf(delaySeconds))));

		target.sendMessage(mm.deserialize(config.getString("tpa.messages.request-accepted")
				.replace("%sender%", sender.getName())));

		// Store sender's initial location
		final UUID senderId = sender.getUniqueId();
		final var startLocation = sender.getLocation().clone();

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			Player current = Bukkit.getPlayer(senderId);
			if (current == null) return;

			// Check if the player has moved
			if (!current.getLocation().getBlock().equals(startLocation.getBlock())) {
				current.sendMessage(mm.deserialize(config.getString("tpa.messages.teleport-cancelled-move")));
				return;
			}

			current.teleport(target.getLocation());
			current.sendMessage(mm.deserialize(config.getString("tpa.messages.teleported")
					.replace("%sender%", sender.getName())
					.replace("%target%", target.getName())));
		}, delayTicks);
	}

	public void denyRequest(Player target) {
		TeleportRequest request = requests.remove(target.getUniqueId());
		if (request == null) return;

		Player sender = request.getSender();
		sender.sendMessage(mm.deserialize(plugin.getConfig().getString("tpa.messages.request-denied")
				.replace("%sender%", target.getName())));
		target.sendMessage(mm.deserialize("<gray>TPA request denied."));
	}

	private void expireRequest(UUID targetUUID) {
		TeleportRequest request = requests.remove(targetUUID);
		if (request != null) {
			Player target = Bukkit.getPlayer(targetUUID);
			if (target != null) {
				target.sendMessage(mm.deserialize(plugin.getConfig().getString("tpa.messages.request-expired")
						.replace("%sender%", request.getSender().getName())));
			}
		}
	}

	private void sendClickableMessage(Player target, Player sender) {
		Component msg = Component.text()
				.append(Component.text("» ").color(net.kyori.adventure.text.format.NamedTextColor.GRAY))
				.append(Component.text("[Accept]").color(net.kyori.adventure.text.format.NamedTextColor.GREEN)
						.clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()))
						.hoverEvent(HoverEvent.showText(Component.text("Click to accept!"))))
				.append(Component.space())
				.append(Component.text("[Deny]").color(net.kyori.adventure.text.format.NamedTextColor.RED)
						.clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()))
						.hoverEvent(HoverEvent.showText(Component.text("Click to deny!"))))
				.build();

		target.sendMessage(msg);
	}

	public boolean hasRequest(Player target) {
		return requests.containsKey(target.getUniqueId());
	}

	public boolean isRequestFrom(Player target, Player sender) {
		TeleportRequest request = requests.get(target.getUniqueId());
		return request != null && request.getSender().equals(sender);
	}
}
