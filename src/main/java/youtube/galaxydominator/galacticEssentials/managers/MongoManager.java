package youtube.galaxydominator.galacticEssentials.managers;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

public class MongoManager {

	private static MongoManager instance;
	private MongoClient mongoClient;
	private MongoDatabase database;

	private MongoManager(String uri, String dbName) {
		try {
			ConnectionString connString = new ConnectionString(uri);
			MongoClientSettings settings = MongoClientSettings.builder()
					.applyConnectionString(connString)
					.build();
			mongoClient = MongoClients.create(settings);
			database = mongoClient.getDatabase(dbName);
			System.out.println("[MongoManager] Connected to MongoDB database: " + dbName);
		} catch (Exception e) {
			System.out.println("[MongoManager] Failed to connect to MongoDB:");
			e.printStackTrace();
		}
	}

	public static void init(String uri, String dbName) {
		if (instance == null) {
			instance = new MongoManager(uri, dbName);
		}
	}

	public static MongoManager getInstance() {
		return instance;
	}

	public MongoCollection<Document> getCollection(String name) {
		if (database == null) {
			throw new IllegalStateException("MongoDatabase is null. MongoDB connection may have failed.");
		}
		return database.getCollection(name);
	}
}
