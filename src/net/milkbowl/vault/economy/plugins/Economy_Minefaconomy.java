package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import me.coniin.plugins.minefaconomy.Minefaconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_Minefaconomy implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final String name = "Minefaconomy";
	private final int fractionalDigits = 2;
	private final String currencyNamePlural = "Minefacoins";
	private final String currencyNameSingular = "Minefacoin";

	private Plugin plugin = null;
	private Minefaconomy economy = null;

	public Economy_Minefaconomy(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager()
				.registerEvents(new EconomyServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (economy == null) {
			Plugin econ = plugin.getServer().getPluginManager()
					.getPlugin("Minefaconomy");
			if (econ != null && econ.isEnabled()) {
				economy = (Minefaconomy) econ;
				log.info(String.format("[%s][Economy] %s hooked.", plugin
						.getDescription().getName(), name));
			}
		}
	}

	public class EconomyServerListener implements Listener {
		Economy_Minefaconomy economy = null;

		public EconomyServerListener(Economy_Minefaconomy economy) {
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.economy == null) {
				Plugin eco = plugin.getServer().getPluginManager()
						.getPlugin("3co");

				if (eco != null) {
					economy.economy = (Minefaconomy) eco;
					log.info(String.format("[%s][Economy] %s hooked.", plugin
							.getDescription().getName(), economy.name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (economy.economy != null) {
				if (event.getPlugin().getDescription().getName()
						.equals("Minefaconomy")) {
					economy.economy = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin
							.getDescription().getName(), economy.name));
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (economy == null) {
			return false;
		} else {
			return economy.isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int fractionalDigits() {
		return fractionalDigits;
	}

	@Override
	public String format(double amount) {
		return economy.format(amount);
	}

	@Override
	public String currencyNamePlural() {
		return currencyNamePlural;
	}

	@Override
	public String currencyNameSingular() {
		return currencyNameSingular;
	}

	@Override
	public boolean hasAccount(String playerName) {
		return economy.hasAccount(playerName);
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		return economy.getBalance(playerName);
	}

	@Override
	public double getBalance(String playerName, String world) {
		return getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return economy.has(playerName, amount);
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (economy.withdrawPlayer(playerName, amount)){
			return new EconomyResponse(0, 0,
					EconomyResponse.ResponseType.SUCCESS, null);
		}
		return new EconomyResponse(amount, 0,
				EconomyResponse.ResponseType.FAILURE, "Not enough Coins!");
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName,
			double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		if (economy.depositPlayer(playerName, amount)){
			return new EconomyResponse(amount, 0,
					EconomyResponse.ResponseType.SUCCESS, null);
		}
		return new EconomyResponse(amount, 0,
				EconomyResponse.ResponseType.FAILURE, "depositError!");
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName,
			double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return economy.createPlayerAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				"MineFaconomy does not support bank accounts!");
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}

}
