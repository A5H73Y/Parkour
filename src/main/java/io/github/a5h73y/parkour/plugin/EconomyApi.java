package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.EconomyConfig;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault Economy integration.
 * When the EconomyAPI class is initialised, an attempt is made to connect to Vault / Economy.
 * If the outcome succeeds and a provider is found, economy will be enabled.
 * If Parkour does not link to an Economy plugin, all attempted purchases will be successful.
 */
public class EconomyApi extends PluginWrapper {

	private Economy economy;

	@Override
	public String getPluginName() {
		return "Vault";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (isEnabled()) {
			RegisteredServiceProvider<Economy> economyProvider =
					getServer().getServicesManager().getRegistration(Economy.class);

			if (economyProvider == null) {
				PluginUtils.log("[Economy] Failed to connect to Vault's Economy service. Disabling Economy.", 2);
				setEnabled(false);
				return;
			}

			economy = economyProvider.getProvider();
		}
	}

	/**
	 * Reward the Player with an amount.
	 *
	 * @param player target player
	 * @param amount amount to reward
	 * @return transaction success
	 */
	public boolean rewardPlayer(Player player, double amount) {
		return this.economy.depositPlayer(player, amount).transactionSuccess();
	}

	/**
	 * Charge the Player an amount.
	 *
	 * @param player target player
	 * @param amount amount to charge
	 * @return transaction success
	 */
	public boolean chargePlayer(Player player, double amount) {
		return this.economy.withdrawPlayer(player, amount).transactionSuccess();
	}

	/**
	 * Check if the Player has the requested amount of currency.
	 *
	 * @param player target player
	 * @param amount amount required
	 * @return player has sufficient amount
	 */
	public boolean hasAmount(Player player, double amount) {
		return this.economy.has(player, amount);
	}

	/**
	 * Get the Economy Currency name.
	 *
	 * @return currency name
	 */
	public String getCurrencyName() {
		return economy.currencyNamePlural() == null ? "" : " " + economy.currencyNamePlural();
	}

	/**
	 * Reward Player for completing Course.
	 * If Economy is enabled, give the Player the amount of currency earned.
	 *
	 * @param player target player
	 * @param courseName course name
	 */
	public void giveEconomyPrize(Player player, String courseName) {
		if (isEnabled()) {
			int reward = CourseInfo.getEconomyFinishReward(courseName);

			if (reward > 0) {
				rewardPlayer(player, reward);

				player.sendMessage(TranslationUtils.getTranslation("Economy.Reward")
						.replace("%AMOUNT%", reward + getCurrencyName())
						.replace(Constants.COURSE_PLACEHOLDER, courseName));
			}
		}
	}

	/**
	 * Display the Economy information.
	 *
	 * @param sender requesting sender
	 */
	public void displayEconomyInformation(CommandSender sender) {
		TranslationUtils.sendHeading("Economy Details", sender);
		TranslationUtils.sendValue(sender, "Enabled", Boolean.toString(isEnabled()));

		if (isEnabled()) {
			TranslationUtils.sendValue(sender, "Economy", economy.getName());
			TranslationUtils.sendValue(sender, "Currency", getCurrencyName());
		}
	}

	/**
	 * Validate and Charge the Player for joining Course.
	 * Check if there is a Join Fee for the Course, and that the player has sufficient funds.
	 * If there is a Fee, charge the Player and continue.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 * @return Player can join course
	 */
	public boolean validateAndChargeCourseJoin(Player player, String courseName) {
		boolean allowed = true;
		if (isEnabled()) {
			int joinFee = CourseInfo.getEconomyJoiningFee(courseName);

			if (joinFee > 0) {
				if (!hasAmount(player, joinFee)) {
					player.sendMessage(TranslationUtils.getTranslation("Economy.Insufficient")
							.replace("%AMOUNT%", joinFee + getCurrencyName())
							.replace(Constants.COURSE_PLACEHOLDER, courseName));
					allowed = false;

				} else {
					chargePlayer(player, joinFee);
					player.sendMessage(TranslationUtils.getTranslation("Economy.Fee")
							.replace("%AMOUNT%", joinFee + getCurrencyName())
							.replace(Constants.COURSE_PLACEHOLDER, courseName));
				}
			}
		}

		return allowed;
	}

	/**
	 * Set the Finish Reward for the Course.
	 *
	 * @param courseName course name
	 * @param value amount to reward
	 */
	public void setFinishReward(String courseName, double value) {
		Parkour.getConfig(ConfigType.ECONOMY).set("Price." + courseName.toLowerCase() + ".Finish", value);
		Parkour.getConfig(ConfigType.ECONOMY).save();
	}

	/**
	 * Set the Joining Fee for the Course.
	 *
	 * @param courseName course name
	 * @param value amount to charge
	 */
	public void setJoinFee(String courseName, double value) {
		Parkour.getConfig(ConfigType.ECONOMY).set("Price." + courseName.toLowerCase() + ".JoinFee", value);
		Parkour.getConfig(ConfigType.ECONOMY).save();
	}

	/**
	 * Process Economy Command input.
	 * Each of the valid commands will be processed based on input.
	 *
	 * @param sender requesting sender
	 * @param args command arguments
	 */
	public void processCommand(CommandSender sender, String... args) {
		if (!isEnabled()) {
			TranslationUtils.sendValueTranslation("Error.PluginNotLinked", getPluginName(), sender);
			return;
		}

		switch (args[1].toLowerCase()) {
			case "setprize":
				processSetPrizeCommand(sender, args);
				break;

			case "setfee":
				processSetFeeCommand(sender, args);
				break;

			case "recreate":
				recreateEconomy(sender);
				break;

			case "info":
				displayEconomyInformation(sender);
				break;

			default:
				TranslationUtils.sendInvalidSyntax(sender, "econ", "(info / recreate / setprize / setfee)");
		}
	}

	private void processSetPrizeCommand(CommandSender sender, String... args) {
		if (args.length != 4) {
			TranslationUtils.sendInvalidSyntax(sender, "econ", "setprize (course) (amount)");
			return;
		}

		if (!Parkour.getInstance().getCourseManager().doesCourseExists(args[2])) {
			TranslationUtils.sendValueTranslation("Error.NoExist", args[2], sender);
			return;
		}

		if (!ValidationUtils.isPositiveDouble(args[3])) {
			TranslationUtils.sendTranslation("Error.InvalidAmount", sender);
			return;
		}

		setFinishReward(args[2], Double.parseDouble(args[3]));
		TranslationUtils.sendPropertySet(sender, "Economy Prize", args[2], args[3]);
	}

	private void processSetFeeCommand(CommandSender sender, String... args) {
		if (args.length != 4) {
			TranslationUtils.sendInvalidSyntax(sender, "econ", "setfee (course) (amount)");
			return;
		}

		if (!Parkour.getInstance().getCourseManager().doesCourseExists(args[2])) {
			TranslationUtils.sendValueTranslation("Error.NoExist", args[2], sender);
			return;
		}

		if (!ValidationUtils.isPositiveDouble(args[3])) {
			TranslationUtils.sendTranslation("Error.InvalidAmount", sender);
			return;
		}

		setJoinFee(args[2], Double.parseDouble(args[3]));
		TranslationUtils.sendPropertySet(sender, "Join Fee", args[2], args[3]);
	}

	/**
	 * Recreate the Economy course details.
	 * Sets a zero-value Joining and Reward amount for the user to easily edit.
	 *
	 * @return courses updated
	 */
	private int recreateEconomy(CommandSender sender) {
		EconomyConfig config = (EconomyConfig) Parkour.getConfig(ConfigType.ECONOMY);
		TranslationUtils.sendMessage(sender, "Starting Recreation...");

		int changes = 0;
		for (String course : CourseInfo.getAllCourseNames()) {
			if (!config.contains(course + ".JoinFee")) {
				changes++;
				config.set(course + ".JoinFee", 0);
			}
			if (!config.contains(course + ".FinishReward")) {
				config.set(course + ".FinishReward", 0);
			}
		}

		config.save();
		TranslationUtils.sendMessage(sender, "Process Complete! &b" + changes + "&f courses updated.");
		return changes;
	}
}
