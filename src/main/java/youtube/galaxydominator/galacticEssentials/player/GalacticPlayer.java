package youtube.galaxydominator.galacticEssentials.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import youtube.galaxydominator.galacticEssentials.settings.BankSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Getter
@Setter
public class GalacticPlayer {

	// Static MongoDB collection (must be set externally)
	@Setter
	private static MongoCollection<Document> collection;

	private UUID uuid;
	private String name;
	private int kills;
	private int deaths;
	private int mined_blocks;
	private double balance;
	private double bankBalance;
	private List<ItemStack> inventory;
	private boolean isStaff;
	private boolean isFrozen;

	// Constructor for new players
	public GalacticPlayer(UUID uuid) {
		this.uuid = uuid;
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		this.name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
		this.kills = 0;
		this.deaths = 0;
		this.mined_blocks = 0;
		this.balance = 0.0;
		this.bankBalance = 0.0;
		this.inventory = new ArrayList<>();
		this.isStaff = false;
		this.isFrozen = false;
	}

	// Constructor from existing MongoDB document
	public GalacticPlayer(Document doc) {
		this.uuid = UUID.fromString(doc.getString("uuid"));
		this.name = doc.getString("name");
		this.kills = doc.getInteger("kills", 0);
		this.deaths = doc.getInteger("deaths", 0);
		this.mined_blocks = doc.getInteger("mined_blocks", 0);
		this.balance = doc.getDouble("balance");
		this.bankBalance = doc.getDouble("bankBalance");
		this.isStaff = doc.getBoolean("isStaff", false);
		this.isFrozen = doc.getBoolean("isFrozen", false);

		List<String> invBase64 = (List<String>) doc.get("inventory");
		this.inventory = invBase64 != null ? deserializeInventory(invBase64) : new ArrayList<>();
	}

	// Convert to MongoDB document
	public Document toDocument() {
		return new Document("uuid", uuid.toString())
				.append("name", name)
				.append("kills", kills)
				.append("deaths", deaths)
				.append("mined_blocks", mined_blocks)
				.append("balance", balance)
				.append("bankBalance", bankBalance)
				.append("isStaff", isStaff)
				.append("isFrozen", isFrozen)
				.append("inventory", serializeInventory(inventory));
	}

	// Save player data to MongoDB
	public void save() {
		if (collection == null) {
			System.out.println("MongoDB collection is not set!");
			return;
		}
		Document doc = toDocument();
		collection.replaceOne(Filters.eq("uuid", uuid.toString()), doc, new ReplaceOptions().upsert(true));
	}

	// Load player data from MongoDB
	public static GalacticPlayer load(UUID uuid) {
		if (collection == null) {
			System.out.println("MongoDB collection is not set!");
			return null;
		}
		Document doc = collection.find(Filters.eq("uuid", uuid.toString())).first();
		return doc != null ? new GalacticPlayer(doc) : null;
	}

	// Serialize inventory to Base64 strings
	private static List<String> serializeInventory(List<ItemStack> items) {
		List<String> serialized = new ArrayList<>();
		for (ItemStack item : items) {
			try (
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					BukkitObjectOutputStream oos = new BukkitObjectOutputStream(byteStream)
			) {
				oos.writeObject(item);
				serialized.add(Base64.getEncoder().encodeToString(byteStream.toByteArray()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return serialized;
	}

	// Deserialize inventory from Base64 strings
	private static List<ItemStack> deserializeInventory(List<String> data) {
		List<ItemStack> items = new ArrayList<>();
		for (String base64 : data) {
			try (
					ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
					BukkitObjectInputStream ois = new BukkitObjectInputStream(byteStream)
			) {
				ItemStack item = (ItemStack) ois.readObject();
				items.add(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return items;
	}

	// Clamp balance to max settings
	public void setBankBalance(double newBalance) {
		this.bankBalance = Math.min(BankSettings.MAX_BALANCE, Math.max(0, newBalance));
	}
}
