package youtube.galaxydominator.galacticEssentials.logs;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class BankLogManager {

	private final MongoCollection<Document> collection;

	public BankLogManager(MongoCollection<Document> collection) {
		this.collection = collection;
	}

	public void log(Document logEntry) {
		collection.insertOne(logEntry);
	}
}
