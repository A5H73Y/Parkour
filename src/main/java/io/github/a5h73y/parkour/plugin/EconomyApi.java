package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.configuration.impl.EconomyConfig;
import io.github.a5h73y.parkour.course.CourseInfo;
import io.github.a5h73y.parkour.course.CourseManager;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * {@link Economy} integration.
 * When the EconomyAPI class is initialised, an attempt is made to connect to Vault / Economy.
 * If the outcome succeeds and a provider is found, economy will be enabled.
 * If Parkour does not link to a Economy plugin, all attempted purchases will be successful.
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

	public boolean rewardPlayer(Player player, double amount) {
		return this.economy.depositPlayer(player, amount).transactionSuccess();
	}

	public boolean chargePlayer(Player player, double amount) {
		return this.economy.withdrawPlayer(player, amount).transactionSuccess();
	}

	public boolean hasAmount(Player player, double amount) {
		return this.economy.has(player, amount);
	}

	/**
	 * Give the player the economy prize for the course.
	 *
	 * @param player
	 * @param courseName
	 */
	public void giveEconomyPrize(Player player, String courseName) {
		if (isEnabled()) {
			int reward = CourseInfo.getEconomyFinishReward(courseName);

			if (reward > 0) {
				rewardPlayer(player, reward);

				player.sendMessage(TranslationUtils.getTranslation("Economy.Reward")
						.replace("%AMOUNT%", reward + getCurrencyName())
						.replace("%COURSE%", courseName));
			}
		}
	}

	/**
	 * Send the player a summary of the Economy information.
	 *
	 * @param sender requesting sender
	 */
	public void sendEconomyInformation(CommandSender sender) {
		TranslationUtils.sendHeading("Economy Details", sender);
		sender.sendMessage("Enabled: " + isEnabled());

		if (isEnabled()) {
			FileConfiguration config = Parkour.getDefaultConfig();
			sender.sendMessage("Economy: " + economy.getName());

			// TODO more
		}
	}

	public boolean validateCourseJoin(Player player, String courseName) {
		boolean allowed = true;
		if (isEnabled()) {
			int joinFee = CourseInfo.getEconomyJoiningFee(courseName);

			if (joinFee > 0) {
				if (!hasAmount(player, joinFee)) {
					player.sendMessage(TranslationUtils.getTranslation("Economy.Insufficient")
							.replace("%AMOUNT%", joinFee + getCurrencyName())
							.replace("%COURSE%", courseName));
					allowed = false;

				} else {
					chargePlayer(player, joinFee);
					player.sendMessage(TranslationUtils.getTranslation("Economy.Fee")
							.replace("%AMOUNT%", joinFee + getCurrencyName())
							.replace("%COURSE%", courseName));
				}
			}
		}

		return allowed;
	}

	public String getCurrencyName() {
		return economy.currencyNamePlural() == null ? "" : " " + economy.currencyNamePlural();
	}

	/**
	 * TODO
	 *
	 * @param args
	 * @param sender
	 */
	public void processEconomyCommand(String[] args, CommandSender sender) {
		if (!isEnabled()) {
			TranslationUtils.sendValueTranslation("Error.PluginNotLinked", getPluginName(), sender);
			return;
		}

		ParkourConfiguration econConfig = Parkour.getConfig(ConfigType.ECONOMY);

		if (args[1].equalsIgnoreCase("info")) {
			sendEconomyInformation(sender);

		} else if (args[1].equalsIgnoreCase("setprize")) {
			if (args.length != 4) {
				TranslationUtils.sendInvalidSyntax(sender, "econ", "setprize (course) (amount)");
				return;
			}
			if (!Parkour.getInstance().getCourseManager().courseExists(args[2])) {
				TranslationUtils.sendValueTranslation("Error.NoExist", args[2], sender);
				return;
			}
			if (!Validation.isPositiveInteger(args[3])) {
				sender.sendMessage(Parkour.getPrefix() + "Amount needs to be numeric.");
				return;
			}

			econConfig.set("Price." + args[2].toLowerCase() + ".Finish", Integer.parseInt(args[3]));
			econConfig.save();
			sender.sendMessage(Parkour.getPrefix() + "Prize for " + args[2] + " set to " + args[3]);

		} else if (args[1].equalsIgnoreCase("setfee")) {
			if (args.length != 4) {
				TranslationUtils.sendInvalidSyntax(sender, "econ", "setfee (course) (amount)");
				return;
			}
			if (!Parkour.getInstance().getCourseManager().courseExists(args[2])) {
				TranslationUtils.sendValueTranslation("Error.NoExist", args[2], sender);
				return;
			}
			if (!Validation.isPositiveInteger(args[3])) {
				sender.sendMessage(Parkour.getPrefix() + "Amount needs to be numeric.");
				return;
			}

			econConfig.set("Price." + args[2].toLowerCase() + ".JoinFee", Integer.parseInt(args[3]));
			econConfig.save();
			sender.sendMessage(Parkour.getPrefix() + "Fee for " + args[2] + " set to " + args[3]);

		} else if (args[1].equalsIgnoreCase("recreate")) {
			sender.sendMessage(Parkour.getPrefix() + "Starting Recreation...");
			int changed = recreateEconomy();
			sender.sendMessage(Parkour.getPrefix() + "Process Complete! " + changed + " courses updated.");

		} else {
			TranslationUtils.sendInvalidSyntax(sender, "econ", "(info / recreate / setprize / setfee)");
		}
	}

	/**
	 * Recreate the courses economy information
	 * This includes the cost to join and the finish prize
	 *
	 * @return int
	 */
	private int recreateEconomy() {
		EconomyConfig config = (EconomyConfig) Parkour.getConfig(ConfigType.ECONOMY);

		int updated = 0;
		for (String course : CourseInfo.getAllCourses()) {
			try {
				if (!config.contains(course + ".JoinFee")) {
					updated++;
					config.set(course + ".JoinFee", 0);
				}
				if (!config.contains(course + ".FinishReward")) {
					config.set(course + ".FinishReward", 0);
				}
			} catch (Exception ex) {
				PluginUtils.log(TranslationUtils.getValueTranslation("Error.Something", ex.getMessage(), false));
			}
		}

		config.save();
		return updated;
	}
}
