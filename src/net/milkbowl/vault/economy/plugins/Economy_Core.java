package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import us.sparknetwork.core.CorePlugin;
import us.sparknetwork.core.economy.EconomyCallback.EconTransaction;

public class Economy_Core extends AbstractEconomy {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final String name = "Spark Economy";
	private Plugin plugin = null;
	private CorePlugin cp = null;

	public Economy_Core(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (cp == null) {
			Plugin core = plugin.getServer().getPluginManager().getPlugin("Core");
			if (core != null && core.isEnabled()) {
				cp = (CorePlugin) core;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (cp == null) {
			return false;
		} else {
			return cp.isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	// use uuids not names
	@Override
	public double getBalance(String playerName) {
		@SuppressWarnings("deprecation")
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		double balance;

		balance = cp.getEconomyManager().hasAccount(uuid) ? cp.getEconomyManager().getBalance(uuid)
				: cp.getEconomyManager().createAccount(uuid).getNewbalance();

		return balance;
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		if (hasAccount(playerName)) {
			return false;
		}
		@SuppressWarnings("deprecation")
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		return cp.getEconomyManager().createAccount(uuid).isSucessfully() == EconTransaction.SUCCESSFULLY;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		@SuppressWarnings("deprecation")
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		double balance;
		EconomyResponse.ResponseType type;
		String errorMessage = null;

		if (cp.getEconomyManager().hasAccount(uuid)) {
			cp.getEconomyManager().withdrawBalance(uuid, amount);
			balance = cp.getEconomyManager().getBalance(uuid);
			type = EconomyResponse.ResponseType.SUCCESS;
		} else {
			if (createPlayerAccount(playerName)) {
				return withdrawPlayer(playerName, amount);
			} else {
				amount = 0;
				balance = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "User does not exist";
			}
		}

		return new EconomyResponse(amount, balance, type, errorMessage);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		@SuppressWarnings("deprecation")
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		double balance;
		EconomyResponse.ResponseType type;
		String errorMessage = null;

		if (cp.getEconomyManager().hasAccount(uuid)) {
			cp.getEconomyManager().addBalance(uuid, amount);
			balance = cp.getEconomyManager().getBalance(uuid);
			type = EconomyResponse.ResponseType.SUCCESS;
		} else {
			if (createPlayerAccount(playerName)) {
				return depositPlayer(playerName, amount);
			} else {
				amount = 0;
				balance = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "User does not exist";
			}
		}
		return new EconomyResponse(amount, balance, type, errorMessage);
	}

	public class EconomyServerListener implements Listener {
		Economy_Core economy = null;

		public EconomyServerListener(Economy_Core economy) {
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.cp == null) {
				Plugin core = event.getPlugin();

				if (core.getDescription().getName().equals("Core")) {
					economy.cp = (CorePlugin) core;
					log.info(
							String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (economy.cp != null) {
				if (event.getPlugin().getDescription().getName().equals("CorePlugin")) {
					economy.cp = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(),
							economy.name));
				}
			}
		}
	}

	@Override
	public String format(double amount) {
		return ((Double) amount).toString();
	}

	@Override
	public String currencyNameSingular() {
		return "";
	}

	@Override
	public String currencyNamePlural() {
		return "";
	}

	@Override
	public boolean has(String playerName, double amount) {
		@SuppressWarnings("deprecation")
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		if (cp.getEconomyManager().hasAccount(uuid)) {
			return cp.getEconomyManager().getBalance(uuid) >= amount;
		} else {
			return false;
		}
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"Spark Eco does not support bank accounts!");
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public boolean hasAccount(String playerName) {
		@SuppressWarnings("deprecation")
		UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
		return cp.getEconomyManager().hasAccount(uuid);
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName, String world) {
		return getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}
}
