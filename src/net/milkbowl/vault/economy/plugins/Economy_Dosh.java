package net.milkbowl.vault.economy.plugins;

import java.util.List;

import org.bukkit.plugin.Plugin;

import com.gravypod.Dosh.Dosh;
import com.gravypod.Dosh.MoneyUtils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;


public class Economy_Dosh implements Economy {
	
	
	Plugin plugin;
	Dosh doshPlugin;
	boolean isEnabled = false;
	DoshAPIHandler apiHandle;
	
	public Economy_Dosh(Plugin _plugin) {
		
		plugin = _plugin;
		
		if (plugin.getServer().getPluginManager().isPluginEnabled("Dosh")) {
			doshPlugin = (Dosh) plugin.getServer().getPluginManager().getPlugin("Dosh");
			apiHandle = new DoshAPIHandler();
			isEnabled = true;
		} else {
			return;
		}
		
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public String getName() {
		return "Dosh";
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public String format(double amount) {
		return null;
	}

	@Override
	public String currencyNamePlural() {
		return Dosh.getSettings().moneyName + "s";
	}

	@Override
	public String currencyNameSingular() {
		return Dosh.getSettings().moneyName;
	}

	@Override
	public boolean hasAccount(String playerName) {
		return true;
	}

	@Override
	public double getBalance(String playerName) {
		return DoshAPIHandler.getUserBal(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return (getBalance(playerName) - amount) > 0;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		
		if (DoshAPIHandler.subtractMoney(playerName, amount)) {
			return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Worked!");
		}
		
		return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Didnt work!");
		
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		DoshAPIHandler.addUserBal(playerName, amount);
		return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "It worked!");
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return null;
	}

	@Override
	public List<String> getBanks() {
		return null;
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return false;
	}
	
	public class DoshAPIHandler extends MoneyUtils {}
	
}
