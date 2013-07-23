package net.milkbowl.vault.economy.plugins;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_EconomyLite implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "EconomyLite";
	private Plugin plugin = null;
	protected el.me.Economy economy = null;
	protected el.me.Main main = null;

	public void EconomyServerListener(el.me.Economy economy_EconomyLite, el.me.Main main) {
		this.economy = economy_EconomyLite;
		this.main = main;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		if (economy == null) {
			Plugin ec = plugin.getServer().getPluginManager().getPlugin("EconomyLite");

			if (ec != null && ec.getClass().getName().equals("el.me.Main")) {
				economy = (el.me.Economy) ec;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		if (economy != null) {
			if (event.getPlugin().getDescription().getName().equals("EconomyLite")) {
				economy = null;
				log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (economy == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public boolean hasBankSupport() {

		return false;
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public String format(double amount) {
		return main.format(new BigDecimal(Double.toString(amount)));
	}

	@Override
	public String currencyNamePlural() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "Settings.yml"));
		if (config.getString("currency name", "$").length() == 1) {
			return config.getString("currency name", "$");
		} else {
			return (config.getString("currency name", " Dollar")) + "s";
		}
	}

	@Override
	public String currencyNameSingular() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "Settings.yml"));
		return config.getString("currency name", "$");
	}

	@Override
	public boolean hasAccount(String playerName) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "Settings.yml"));
		if (config.isSet(playerName)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		return economy.getMoney(playerName).doubleValue();
	}

	@Override
	public double getBalance(String playerName, String world) {
		return economy.getMoney(playerName).doubleValue();
	}

	@Override
	public boolean has(String playerName, double amount) {
		if (economy.getMoney(playerName).doubleValue() >= amount) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		if (economy.getMoney(playerName).doubleValue() >= amount) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (economy.canTake(playerName, new BigDecimal(Double.toString(amount)))) {
			economy.money(playerName, new BigDecimal(Double.toString(amount)), "take", true);
			return new EconomyResponse(amount, economy.getMoney(playerName).doubleValue(), ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, economy.getMoney(playerName).doubleValue(), ResponseType.FAILURE, "Insufficient funds");
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount, boolean sendMessage) {
		if (economy.canTake(playerName, new BigDecimal(Double.toString(amount)))) {
			economy.money(playerName, new BigDecimal(Double.toString(amount)), "take", sendMessage);
			return new EconomyResponse(amount, economy.getMoney(playerName).doubleValue(), ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, economy.getMoney(playerName).doubleValue(), ResponseType.FAILURE, "Insufficient funds");
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		economy.money(playerName, new BigDecimal(Double.toString(amount)), "give", true);
		return new EconomyResponse(amount, economy.getMoney(playerName).doubleValue(), ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount, boolean sendMessage) {
		economy.money(playerName, new BigDecimal(Double.toString(amount)), "give", sendMessage);
		return new EconomyResponse(amount, economy.getMoney(playerName).doubleValue(), ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankBalance(String name) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconomyLite does not support bank accounts!");
	}

	@Override
	public List<String> getBanks() {

		return new ArrayList<String>();
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "Settings.yml"));
		economy.money(playerName, new BigDecimal(config.getString("default value", "100")).setScale(2, RoundingMode.HALF_UP), "reset", false);
		return true;
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

}
