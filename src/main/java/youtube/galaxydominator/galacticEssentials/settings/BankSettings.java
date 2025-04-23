package youtube.galaxydominator.galacticEssentials.settings;

import org.bukkit.configuration.file.FileConfiguration;

public class BankSettings {
	public static double MAX_BALANCE = 100000.0;
	public static double INTEREST_RATE = 0.05;

	public static String MESSAGE_BALANCE = "<gray>Your bank balance: <green>$%amount%";
	public static String MESSAGE_DEPOSIT = "<green>You deposited $%amount% into your bank.";
	public static String MESSAGE_WITHDRAW_FAIL = "<red>You don't have enough in your bank!";

	public static void load(FileConfiguration config) {
		MAX_BALANCE = config.getDouble("settings.max-balance", 100000.0);
		INTEREST_RATE = config.getDouble("settings.interest-rate", 0.05);

		MESSAGE_BALANCE = config.getString("messages.balance", MESSAGE_BALANCE);
		MESSAGE_DEPOSIT = config.getString("messages.deposit-success", MESSAGE_DEPOSIT);
		MESSAGE_WITHDRAW_FAIL = config.getString("messages.withdraw-fail", MESSAGE_WITHDRAW_FAIL);
	}
}
