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

import com.github.winneonsword.WCE.MainWCE;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_WCEconomy implements Economy {
	
	private static Logger log = Logger.getLogger("Minecraft");
	private final String name = "WCEconomy";
	private Plugin plugin = null;
	private MainWCE wce;
	
	public Economy_WCEconomy(Plugin plugin){
		
		this.plugin = plugin;
		
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

		if (wce == null){
			
			Plugin WCEconomy = plugin.getServer().getPluginManager().getPlugin("Essentials");
			
			if (WCEconomy != null && WCEconomy.isEnabled()){
				
				wce = (MainWCE) WCEconomy;
				
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
				
			}
			
		}
		
	}
	
	@Override
	public boolean isEnabled(){
		
		if (wce == null){
			
			return false;
			
		} else {
			
			return wce.isEnabled();
			
		}
		
	}

	@Override
	public String getName(){
		
		return name;
		
	}
	
	@Override
	public double getBalance(String player){
		
		return com.github.winneonsword.WCE.api.EconAPI.getBalance(player);
		
	}
	
	@Override
	public boolean createPlayerAccount(String player){
		
		boolean userExists = com.github.winneonsword.WCE.api.EconAPI.hasAmount(player, 0);
		
		if (userExists){
			
			return false;
			
		}
		
		return com.github.winneonsword.WCE.api.EconAPI.createAccount(player);
		
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String player, double amount){
		
		if (amount < 0){
			
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Not allowed to withdraw negative currency!");
			
		}
		
		com.github.winneonsword.WCE.api.EconAPI.withdrawAmount(player, amount);
		
		double balance = com.github.winneonsword.WCE.api.EconAPI.getBalance(player);
		ResponseType type = ResponseType.SUCCESS;
		String errorMessage = null;
		
		return new EconomyResponse(amount, balance, type, errorMessage);
		
	}
	
	@Override
	public EconomyResponse depositPlayer(String player, double amount){
		
		if (amount < 0){
			
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Not allowed to deposit negative currency!");
			
		}
		
		com.github.winneonsword.WCE.api.EconAPI.depositAmount(player, amount);
		
		double balance = com.github.winneonsword.WCE.api.EconAPI.getBalance(player);
		ResponseType type = ResponseType.SUCCESS;
		String errorMessage = null;
		
		return new EconomyResponse(amount, balance, type, errorMessage);
		
	}
	
	@Override
	public String format(double amount){
		
		return com.github.winneonsword.WCE.api.EconAPI.format(amount);
		
	}
	
	@Override
	public String currencyNameSingular(){
		
		return "";
		
	}
	
	@Override
	public String currencyNamePlural(){
		
		return "";
		
	}
	
	@Override
	public boolean has(String player, double amount){
		
		return com.github.winneonsword.WCE.api.EconAPI.hasAmount(player, amount);
		
	}
	
	@Override
	public EconomyResponse createBank(String name, String player){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse deleteBank(String name){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse bankHas(String name, double amount){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse bankWithdraw(String name, double amount){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse bankDeposit(String name, double amount){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse isBankOwner(String name, String player){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse isBankMember(String name, String player){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public EconomyResponse bankBalance(String name){
		
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "WCEconomy does not support bank accounts!");
		
	}
	
	@Override
	public List<String> getBanks(){
		
		return new ArrayList<String>();
		
	}
	
	@Override
	public boolean hasBankSupport(){
		
		return false;
		
	}
	
	@Override
	public boolean hasAccount(String player){
		
		return true;
		
	}
	
	@Override
	public int fractionalDigits(){
		
		return -1;
		
	}
	
	@Override
	public boolean hasAccount(String player, String world){
		
		return true;
		
	}
	
	@Override
	public double getBalance(String player, String world){
		
		return getBalance(player);
		
	}
	
	@Override
	public boolean has(String player, String world, double amount){
		
		return has(player, amount);
		
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount){
		
		return withdrawPlayer(player, amount);
		
	}
	
	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount){
		
		return depositPlayer(player, amount);
		
	}
	
	@Override
	public boolean createPlayerAccount(String player, String world){
		
		return createPlayerAccount(player);
		
	}
	
	public class EconomyServerListener implements Listener {
		
		Economy_WCEconomy economy = null;

		public EconomyServerListener(Economy_WCEconomy economy){
			
			this.economy = economy;
			
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event){
			
			if (economy.wce == null){
				
				Plugin WCEconomy = event.getPlugin();

				if (WCEconomy.getDescription().getName().equals("WCEconomy")){
					
					economy.wce = (MainWCE) WCEconomy;
					
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				
				}
				
			}
			
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event){
			
			if (economy.wce != null) {
				
				if (event.getPlugin().getDescription().getName().equals("WCEconomy")){
					
					economy.wce = null;
					
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
				
				}
			}
			
		}
		
	}
	
}
