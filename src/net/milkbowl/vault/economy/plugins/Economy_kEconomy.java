package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.KeybordPiano459.kEconomy.Money;
import me.KeybordPiano459.kEconomy.kEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Economy_kEconomy implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private final String name = "kEconomy";
    private Plugin plugin = null;
    private kEconomy economy = null;
    
    public Economy_kEconomy(Plugin plugin) {
    	this.plugin = plugin;
    	
    	if (economy == null) {
    		Plugin kEconomy = plugin.getServer().getPluginManager().getPlugin("kEconomy");
    		if (kEconomy != null && kEconomy.isEnabled()) {
    			economy = (kEconomy) kEconomy;
    			log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
    		}
    	}
    }
    
    public class EconomyServerListener implements Listener {
    	Economy_kEconomy economy = null;
    	public EconomyServerListener(Economy_kEconomy economy) {
    		this.economy = economy;
    	}
    	
    	@EventHandler(priority = EventPriority.MONITOR)
    	public void onPluginEnable(PluginEnableEvent event) {
    		if (economy.economy == null) {
    			Plugin kecon = plugin.getServer().getPluginManager().getPlugin("kEconomy");
    			if (kecon != null && kecon.isEnabled()) {
    				economy.economy = (kEconomy) kecon;
    				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
    			}
    		}
    	}
    	
    	@EventHandler(priority = EventPriority.MONITOR)
    	public void onPluginDisable(PluginDisableEvent event) {
    		if (economy.economy != null) {
    			if (event.getPlugin().getDescription().getName().equals("kEconomy")) {
    				economy.economy = null;
    				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
    			}
    		}
    	}
    }
    
    public boolean isEnabled() {
    	if (economy.isEnabled()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public String getName() {
    	return name;
    }
    
    public boolean hasBankSupport() {
    	return false;
    }
    
    public int fractionalDigits() {
    	return -1;
    }
    
    public String format(double amount) {
    	return "$" + amount;
    }
    
    public String currencyNamePlural() {
    	return "Dollars";
    }
    
    public String currencyNameSingular() {
    	return "Dollar";
    }
    
    public boolean hasAccount(String player) {
    	return Money.hasAccount(player);
    }
    
    public double getBalance(String player) {
    	return Money.getMoney(player);
    }
    
    public boolean has(String player, double amount) {
    	return Money.getMoney(player) >= amount;
    }
    
    public EconomyResponse withdrawPlayer(String player, double amount) {
    	ResponseType rt;
    	String message;
    	
    	if (Money.hasAccount(player)) {
    		Money.subtractMoney(player, amount);
    		rt = ResponseType.SUCCESS;
    		message = null;
    	} else {
    		rt = ResponseType.FAILURE;
    		message = "Not enough money";
    	}
    	
    	return new EconomyResponse(amount, Money.getMoney(player), rt, message);
    }
    
    public EconomyResponse depositPlayer(String player, double amount) {
    	Money.addMoney(player, amount);
    	return new EconomyResponse(amount, Money.getMoney(player), ResponseType.SUCCESS, "Successfully deposited");
    }
    
    public EconomyResponse createBank(String name, String player) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse deleteBank(String name) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse bankBalance(String name) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse bankHas(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse bankWithdraw(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse bankDeposit(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse isBankOwner(String name, String playername) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public EconomyResponse isBankMember(String name, String playername) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "kEconomy does not support bank accounts");
    }
    
    public List<String> getBanks() {
    	return new ArrayList<String>();
    }
    
    public boolean createPlayerAccount(String player) {
    	if (Money.hasAccount(player)) {
    		return false;
    	} else {
    		Money.createAccount(name);
    		return true;
    	}
    }
}