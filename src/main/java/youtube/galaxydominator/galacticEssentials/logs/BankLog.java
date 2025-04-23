package youtube.galaxydominator.galacticEssentials.logs;

import org.bson.Document;

import java.util.Date;
import java.util.UUID;

public class BankLog {

	public static Document createLog(UUID playerUUID, String playerName, String action, double amount, String performedBy, boolean isAdminAction) {
		return new Document("timestamp", new Date())
				.append("playerUUID", playerUUID.toString())
				.append("playerName", playerName)
				.append("action", action)
				.append("amount", amount)
				.append("performedBy", performedBy)
				.append("admin", isAdminAction);
	}
}
