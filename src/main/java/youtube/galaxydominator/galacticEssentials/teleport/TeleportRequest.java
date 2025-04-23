package youtube.galaxydominator.galacticEssentials.teleport;

import org.bukkit.entity.Player;

public class TeleportRequest {
	private final Player sender;
	private final Player target;

	public TeleportRequest(Player sender, Player target) {
		this.sender = sender;
		this.target = target;
	}

	public Player getSender() {
		return sender;
	}

	public Player getTarget() {
		return target;
	}
}
