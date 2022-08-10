package io.github.a5h73y.parkour.plugin;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class ParkoinsVault extends AbstractEconomy {

	private Parkour parkour;

	public ParkoinsVault(final Parkour parkour) {
		this.parkour = parkour;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public String getName() {
		return "Parkoins (Parkour)";
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 2;
	}

	@Override
	public String format(double amount) {
		return String.valueOf(amount);
	}

	@Override
	public String currencyNamePlural() {
		return "Parkoins";
	}

	@Override
	public String currencyNameSingular() {
		return "Parkoin";
	}

	@Override
	public boolean hasAccount(String playerName) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		return PlayerConfig.hasPlayerConfig(player);
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		return getPlayerConfig(playerName).getParkoins();
	}

	@Override
	public double getBalance(String playerName, String world) {
		return getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (!hasAccount(playerName)) {
			return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "User doesn't have an account.");
		} else if (!has(playerName, amount)) {
			return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "User doesn't have enough Parkoins.");
		} else {
			getPlayerConfig(playerName).decreaseParkoins(amount);
			return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		// if they don't have an account - lets assume they haven't done anything Parkour related
		if (!hasAccount(playerName)) {
			return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "User doesn't have an account.");
		} else if (amount < 0) {
			return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Amount must be positive.");
		}
		getPlayerConfig(playerName).increaseParkoins(amount);
		return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return notImplemented();
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return notImplemented();
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return notImplemented();
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return notImplemented();
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return notImplemented();
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return notImplemented();
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return notImplemented();
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return notImplemented();
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		if (hasAccount(playerName)) {
			return false;
		} else {
			getPlayerConfig(playerName).setParkoins(0);
			return true;
		}
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

	private PlayerConfig getPlayerConfig(String playerName) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		return parkour.getConfigManager().getPlayerConfig(player);
	}
	
	private EconomyResponse notImplemented() {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, 
				"This feature is not implemented.");
	}
}
