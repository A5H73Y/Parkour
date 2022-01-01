package io.github.a5h73y.parkour.plugin;

import static io.github.a5h73y.parkour.other.ParkourConstants.AMOUNT_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;
import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseConfig;
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

	public EconomyApi(Parkour parkour) {
		super(parkour);
	}

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

	public boolean isEconomyLinked() {
		return this.economy != null;
	}

	/**
	 * Reward the Player with an amount.
	 *
	 * @param player target player
	 * @param amount amount to reward
	 * @return transaction success
	 */
	public boolean rewardPlayer(Player player, double amount) {
		return isEconomyLinked() && this.economy.depositPlayer(player, amount).transactionSuccess();
	}

	/**
	 * Charge the Player an amount.
	 *
	 * @param player target player
	 * @param amount amount to charge
	 * @return transaction success
	 */
	public boolean chargePlayer(Player player, double amount) {
		return isEconomyLinked() && this.economy.withdrawPlayer(player, amount).transactionSuccess();
	}

	/**
	 * Check if the Player has the requested amount of currency.
	 *
	 * @param player target player
	 * @param amount amount required
	 * @return player has sufficient amount
	 */
	public boolean hasAmount(Player player, double amount) {
		return isEconomyLinked() && this.economy.has(player, amount);
	}

	/**
	 * Get the Economy Currency name.
	 *
	 * @return currency name
	 */
	public String getCurrencyName() {
		return !isEconomyLinked() || economy.currencyNamePlural() == null ? "" : " " + economy.currencyNamePlural();
	}

	/**
	 * Reward Player for completing Course.
	 * If Economy is enabled, give the Player the amount of currency earned.
	 *
	 * @param player target player
	 * @param courseName course name
	 */
	public void giveEconomyPrize(Player player, String courseName) {
		if (isEconomyLinked()) {
			double reward = CourseConfig.getConfig(courseName).getEconomyFinishReward();

			if (reward > 0) {
				rewardPlayer(player, reward);

				player.sendMessage(TranslationUtils.getTranslation("Economy.Reward")
						.replace(AMOUNT_PLACEHOLDER, reward + getCurrencyName())
						.replace(COURSE_PLACEHOLDER, courseName));
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
		TranslationUtils.sendValue(sender, "Enabled", String.valueOf(isEnabled()));

		if (isEnabled()) {
			TranslationUtils.sendValue(sender, "Economy", economy.getName());
			TranslationUtils.sendValue(sender, "Currency Name", getCurrencyName());
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
		if (isEconomyLinked()) {
			double joinFee = CourseConfig.getConfig(courseName).getEconomyJoiningFee();

			if (joinFee > 0) {
				if (!hasAmount(player, joinFee)) {
					player.sendMessage(TranslationUtils.getTranslation("Economy.Insufficient")
							.replace(AMOUNT_PLACEHOLDER, joinFee + getCurrencyName())
							.replace(COURSE_PLACEHOLDER, courseName));
					allowed = false;

				} else {
					chargePlayer(player, joinFee);
					player.sendMessage(TranslationUtils.getTranslation("Economy.Fee")
							.replace(AMOUNT_PLACEHOLDER, joinFee + getCurrencyName())
							.replace(COURSE_PLACEHOLDER, courseName));
				}
			}
		}

		return allowed;
	}

	/**
	 * Process Economy Command input.
	 * Each of the valid commands will be processed based on input.
	 *
	 * @param sender requesting sender
	 * @param args command arguments
	 */
	public void processCommand(CommandSender sender, String... args) {
		if (!isEconomyLinked()) {
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

			case "info":
				displayEconomyInformation(sender);
				break;

			default:
				TranslationUtils.sendInvalidSyntax(sender, "econ", "(info / setprize / setfee)");
		}
	}

	private void processSetPrizeCommand(CommandSender sender, String... args) {
		if (args.length != 4) {
			TranslationUtils.sendInvalidSyntax(sender, "econ", "setprize (course) (amount)");
			return;
		}

		if (!Parkour.getInstance().getCourseManager().doesCourseExist(args[2])) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[2], sender);
			return;
		}

		if (!ValidationUtils.isPositiveDouble(args[3])) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
			return;
		}

		CourseConfig.getConfig(args[2]).setEconomyFinishReward(Double.parseDouble(args[3]));
		TranslationUtils.sendPropertySet(sender, "Economy Prize", args[2], args[3]);
	}

	private void processSetFeeCommand(CommandSender sender, String... args) {
		if (args.length != 4) {
			TranslationUtils.sendInvalidSyntax(sender, "econ", "setfee (course) (amount)");
			return;
		}

		if (!Parkour.getInstance().getCourseManager().doesCourseExist(args[2])) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[2], sender);
			return;
		}

		if (!ValidationUtils.isPositiveDouble(args[3])) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
			return;
		}

		CourseConfig.getConfig(args[2]).setEconomyJoiningFee(Double.parseDouble(args[3]));
		TranslationUtils.sendPropertySet(sender, "Join Fee", args[2], args[3]);
	}
}
