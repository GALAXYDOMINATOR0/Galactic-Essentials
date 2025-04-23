package youtube.galaxydominator.galacticEssentials;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import youtube.galaxydominator.galacticEssentials.commands.*;
import youtube.galaxydominator.galacticEssentials.listeners.ChatListener;
import youtube.galaxydominator.galacticEssentials.listeners.NightListener;
import youtube.galaxydominator.galacticEssentials.listeners.PlayerListener;
import youtube.galaxydominator.galacticEssentials.logs.BankLogManager;
import youtube.galaxydominator.galacticEssentials.managers.GalacticPlayerManager;
import youtube.galaxydominator.galacticEssentials.managers.MongoManager;
import youtube.galaxydominator.galacticEssentials.managers.WarpManager;
import youtube.galaxydominator.galacticEssentials.settings.BankSettings;
import youtube.galaxydominator.galacticEssentials.teleport.TeleportManager;

import java.io.File;
import java.io.IOException;

@Getter
public class GalacticEssentials extends JavaPlugin {


	public MongoManager mongoManager;
	public GalacticPlayerManager playerManager;
	private PaperCommandManager commandManager;
	private BankLogManager bankLogManager;
	private WarpManager warpManager;
	private TeleportManager teleportManager;
	@Getter
	private FileConfiguration bankConfig, warpsConfig;
	private File bankConfigFile, warpsConfigFile;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		createBankConfig();
		createWarpsConfig();


		String mongoUri = getConfig().getString("mongo.uri");
		String dbName = getConfig().getString("mongo.database");

		// Initialize MongoManager
		MongoManager.init(mongoUri, dbName);

		// Example usage: get a collection
		try {
			var collection = MongoManager.getInstance().getCollection("profiles");
			bankLogManager = new BankLogManager(MongoManager.getInstance().getCollection("bank_logs"));
			getLogger().info("Successfully loaded MongoDB collection: players");
		} catch (Exception e) {
			getLogger().severe("Failed to load MongoDB collection: players");
			e.printStackTrace();
		}
		playerManager = GalacticPlayerManager.getInstance();
		BankSettings.load(bankConfig);
		warpManager = new WarpManager(this);
		teleportManager = new TeleportManager(this);


		registerCommands();


		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		getServer().getPluginManager().registerEvents(new NightListener(this), this);
	}

	@Override
	public void onDisable() {
		saveBankConfig();
	}


	private void registerCommands() {
		commandManager = new PaperCommandManager(this);
		commandManager.registerCommand(new BalanceCommand());
		commandManager.registerCommand(new EconomyCommands());
		commandManager.registerCommand(new PayCommand());
		commandManager.registerCommand(new BankCommand());
		commandManager.registerCommand(new WarpCommands(this));
		commandManager.registerCommand(new HealCommand());
		commandManager.registerCommand(new TeleportCommand(this));
		commandManager.registerCommand(new PingCommand());

		commandManager.getCommandCompletions().registerCompletion("warps", c -> {
			return getWarpManager().getAllWarpNames(); // We'll add this method next
		});
	}

	public void createBankConfig() {
		bankConfigFile = new File(getDataFolder(), "bank.yml");

		if (!bankConfigFile.exists()) {
			bankConfigFile.getParentFile().mkdirs();
			saveResource("bank.yml", false); // Will copy default from resources if exists
		}

		bankConfig = YamlConfiguration.loadConfiguration(bankConfigFile);
	}

	public void createWarpsConfig() {
		warpsConfigFile = new File(getDataFolder(), "warps.yml");

		if (!warpsConfigFile.exists()) {
			warpsConfigFile.getParentFile().mkdirs();
			saveResource("warps.yml", false); // Will copy default from resources if exists
		}

		warpsConfig = YamlConfiguration.loadConfiguration(warpsConfigFile);
	}

	public void saveWarpsConfig() {
		try {
			warpsConfig.save(warpsConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveBankConfig() {
		try {
			bankConfig.save(bankConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
