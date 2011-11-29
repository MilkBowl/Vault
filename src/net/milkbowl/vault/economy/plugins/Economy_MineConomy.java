package net.milkbowl.vault.economy.plugins;

import java.util.logging.Logger;

import me.mjolnir.mineconomy.Accounting;
import me.mjolnir.mineconomy.MineConomy;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_MineConomy implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");

	private String name = "MineConomy";
	private Plugin plugin = null;
	private PluginManager pluginManager = null;
	private MineConomy econ = null;
	private EconomyServerListener economyServerListener = null;

	public Economy_MineConomy(Plugin plugin) {
		this.plugin = plugin;
		pluginManager = this.plugin.getServer().getPluginManager();

		economyServerListener = new EconomyServerListener(this);

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

		// Load Plugin in case it was loaded before
		if (econ == null) {
			Plugin econ = plugin.getServer().getPluginManager().getPlugin("MineConomy");
			if (econ != null && econ.isEnabled()) {
				this.econ = (MineConomy) econ;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	private class EconomyServerListener extends ServerListener {
		Economy_MineConomy economy = null;

		public EconomyServerListener(Economy_MineConomy economy) {
			this.economy = economy;
		}

		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.econ == null) {
				Plugin eco = plugin.getServer().getPluginManager().getPlugin("MineConomy");

				if (eco != null && eco.isEnabled()) {
					economy.econ = (MineConomy) eco;
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

		public void onPluginDisable(PluginDisableEvent event) {
			if (economy.econ != null) {
				if (event.getPlugin().getDescription().getName().equals("MineConomy")) {
					economy.econ = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return this.econ != null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String format(double amount) {
		return String.valueOf(amount);
	}

	@Override
	public double getBalance(String playerName) {
		return Accounting.getBalance(playerName, MineConomy.accounts);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		double balance = getBalance(playerName);
		if (amount < 0) {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
		} else if (balance >= amount) {
			double finalBalance = balance - amount;
			Accounting.write(playerName, finalBalance, MineConomy.accounts);
			return new EconomyResponse(amount, finalBalance, ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds");
		}
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		double balance = getBalance(playerName);
		if (amount < 0) {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot deposit negative funds");
		} else {
			balance += amount;
			Accounting.write(playerName, balance, MineConomy.accounts);
			return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
		}
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
	}
}
