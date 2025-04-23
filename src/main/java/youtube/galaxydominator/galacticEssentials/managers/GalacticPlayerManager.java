package youtube.galaxydominator.galacticEssentials.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import youtube.galaxydominator.galacticEssentials.managers.MongoManager;
import youtube.galaxydominator.galacticEssentials.player.GalacticPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GalacticPlayerManager {

	private static final GalacticPlayerManager instance = new GalacticPlayerManager();
	private final Map<UUID, GalacticPlayer> playerCache = new HashMap<>();

	private final MongoCollection<Document> collection;

	private GalacticPlayerManager() {
		// Get the collection from MongoManager (make sure this is initialized properly)
		this.collection = MongoManager.getInstance().getCollection("profiles");
	}

	public static GalacticPlayerManager getInstance() {
		return instance;
	}

	// Cache player in memory
	public void cache(UUID uuid, GalacticPlayer player) {
		playerCache.put(uuid, player);
	}

	// Get cached player
	public GalacticPlayer get(UUID uuid) {
		return playerCache.get(uuid);
	}

	// Load from MongoDB and cache
	public GalacticPlayer load(UUID uuid) {
		Document doc = collection.find(Filters.eq("_id", uuid.toString())).first();

		GalacticPlayer player;
		if (doc == null) {
			// Create new player document if not found
			doc = createNewDocument(uuid);
			collection.insertOne(doc);
			player = new GalacticPlayer(doc);
		} else {
			player = new GalacticPlayer(doc);
		}

		cache(uuid, player);
		return player;
	}

	// Save player to MongoDB
	public void save(GalacticPlayer player) {
		if (player != null) {
			Document doc = player.toDocument(); // You need to implement this in GalacticPlayer
			collection.replaceOne(Filters.eq("_id", player.getUuid().toString()), doc, new com.mongodb.client.model.ReplaceOptions().upsert(true));
		}
	}

	// Save using Bukkit Player
	public GalacticPlayer save(Player player) {
		GalacticPlayer galacticPlayer = get(player.getUniqueId());
		if (galacticPlayer != null) {
			save(galacticPlayer);
		}
		return galacticPlayer;
	}

	// Remove player from cache
	public void remove(UUID uuid) {
		playerCache.remove(uuid);
	}

	// Helper method to create new player data
	private Document createNewDocument(UUID uuid) {
		Document doc = new Document("_id", uuid.toString());
		doc.append("balance", 0.0); // default value
		doc.append("name", "Unknown");
		doc.append("kits", new Document()); // or an empty list if needed
		return doc;
	}
}
