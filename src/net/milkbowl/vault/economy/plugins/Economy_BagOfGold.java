package net.milkbowl.vault.economy.plugins;

import java.util.List;
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
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.BagOfGoldEconomy;

public class Economy_BagOfGold extends AbstractEconomy {

	private static final Logger log = Logger.getLogger("Minecraft");

	private String name = "BagOfGold";
	private Plugin plugin = null;
	protected BagOfGoldEconomy economy = null;

	public Economy_BagOfGold(Plugin plugin) {
		this.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

		// Load Plugin in case it was not loaded before
		if (economy == null) {
			BagOfGold bagofgold = (BagOfGold) plugin.getServer().getPluginManager().getPlugin("BagOfGold");
			if (bagofgold != null && bagofgold.isEnabled()) {
				economy = new BagOfGoldEconomy((BagOfGold) plugin);
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (economy == null) {
			return false;
		} else {
			return ((Plugin) economy).isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String format(double amount) {
		return economy.format(amount);
	}

	@Override
	public String currencyNamePlural() {
		return economy.currencyNamePlural();
	}

	@Override
	public String currencyNameSingular() {
		return economy.currencyNameSingular();
	}

	@Override
	public int fractionalDigits() {
		return economy.fractionalDigits();
	}

	@Override
	public boolean createPlayerAccount(String playername) {
		return economy.createPlayerAccount(playername);
	}

	@Override
	public boolean createPlayerAccount(String playername, String world) {
		return economy.createPlayerAccount(playername, playername);
	}

	@Override
	public double getBalance(String playername) {
		return economy.getBalance(playername);
	}

	@Override
	public double getBalance(String playername, String world) {
		return economy.getBalance(playername, world);
	}

	@Override
	public boolean has(String playername, double amount) {
		return economy.has(playername, amount);
	}

	@Override
	public boolean has(String playername, String world, double amount) {
		return economy.has(playername, world, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playername, double amount) {
		return economy.depositPlayer(playername, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playername, String world, double amount) {
		return economy.depositPlayer(playername, world, amount);
	}

	@Override
	public boolean hasBankSupport() {
		return economy.hasBankSupport();
	}

	@Override
	public List<String> getBanks() {
		return economy.getBanks();
	}

	@Override
	public EconomyResponse createBank(String account, String playername) {
		return economy.createBank(account, playername);
	}

	@Override
	public EconomyResponse deleteBank(String account) {
		return economy.deleteBank(account);
	}

	@Override
	public EconomyResponse bankHas(String account, double amount) {
		return economy.bankHas(account, amount);
	}

	@Override
	public boolean hasAccount(String playername) {
		return economy.hasAccount(playername);
	}

	@Override
	public boolean hasAccount(String playername, String world) {
		return economy.hasAccount(playername, world);
	}

	@Override
	public EconomyResponse bankBalance(String playername) {
		return economy.bankBalance(playername);
	}

	@Override
	public EconomyResponse isBankOwner(String account, String playername) {
		return economy.isBankOwner(account, playername);
	}

	@Override
	public EconomyResponse isBankMember(String account, String playername) {
		return economy.isBankMember(account, playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse withdrawPlayer(String playername, double amount) {
		return economy.withdrawPlayer(playername, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse withdrawPlayer(String playername, String world, double amount) {
		return economy.withdrawPlayer(playername, world, amount);
	}

	@Override
	public EconomyResponse bankDeposit(String playername, double world) {
		return economy.bankDeposit(playername, world);
	}

	@Override
	public EconomyResponse bankWithdraw(String playername, double amount) {
		return economy.bankWithdraw(playername, amount);
	}

	public class EconomyServerListener implements Listener {
		Economy_BagOfGold economy = null;

		public EconomyServerListener(Economy_BagOfGold economy) {
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			Plugin eco = event.getPlugin();
			if (eco.getDescription().getName().equals("BagOfGold")) {
				if (economy.economy == null) {
					BagOfGold bagofgold = (BagOfGold) plugin.getServer().getPluginManager().getPlugin("BagOfGold");
					economy.economy = new BagOfGoldEconomy(bagofgold);
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin().getDescription().getName().equals("BagOfGold")) {
				if (economy != null && economy.economy != null) {
					economy.economy = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(),
							economy.name));
				}
			}
		}
	}

}
