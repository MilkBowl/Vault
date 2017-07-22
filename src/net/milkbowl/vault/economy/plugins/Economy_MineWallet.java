package net.milkbowl.vault.economy.plugins;

import net.minewallet.FileManager;
import net.minewallet.Methods;
import net.minewallet.MineWallet;
import net.minewallet.Mysql_methods;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Economy_MineWallet extends AbstractEconomy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "MineWallet";
    private Plugin plugin = null;
    private MineWallet economy = null;
    private FileManager adatok = FileManager.getInstance();
    private Mysql_methods Mysql = Mysql_methods.getInstance();
	private Methods met = Methods.getInstance();

    public Economy_MineWallet(Plugin plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        if (economy == null) {
            Plugin minewallet = plugin.getServer().getPluginManager().getPlugin(name);

            if (minewallet != null && minewallet.isEnabled()) {
                economy = (MineWallet) minewallet;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return economy != null;
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
    public String format(double amount)
    {
    	
       if (amount <= 1 && amount >= 0)
       {
    	   return String.format(Double.toString(amount) + " %s", currencyNameSingular());
       } 
       else 
       {
    	   return String.format(Double.toString(amount) + " %s", currencyNamePlural());
       }
    }

    @Override
    public String currencyNamePlural() {
        return met.currencyPlural();
    }

    @Override
    public String currencyNameSingular() {
        return met.currencySingular();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return true;
    }

    @Override
    public double getBalance(String playerName) {
        
    	double balance = 0;
    	
    	if(!Mysql.mysql)
        {
    		if(adatok.getData().getInt("players." + playerName + ".bank.bankcarduse") != 1)
    		{
    			balance = met.getPlayerBalance(playerName);
    		}
    		else
    		{
    			balance = met.getPlayerBankBalance(playerName);
    		}
        }
    	else
    	{
    		Mysql.openConnection();
    		if(!Mysql.MysqlRowGetDataBoolean("Bankcarduse", met.getPlayer(playerName)))
    		{
    			balance = Mysql.MysqlRowGetDataDouble("Money", met.getPlayer(playerName));
    		}
    		else
    		{
    			balance = Mysql.MysqlRowGetDataDouble("Bank money", met.getPlayer(playerName));
    		}
    		Mysql.closeConnection();
    	}
    	return balance;
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        ResponseType rt;
        String message;
        
        if(!Mysql.mysql)
        {
        	if(adatok.getData().getInt("players." + playerName + ".bank.bankcarduse") == 1)
        	{
        		double balance = adatok.getData().getDouble("players." + playerName + ".bank.money");
	        	if(balance >= amount)
	        	{
	        		balance = balance - amount;
	        		adatok.getData().set("players." + playerName + ".bank.money", balance);
	        		adatok.saveData();
	        		rt = ResponseType.SUCCESS;
	                message = null;
	        	}
	        	else
	        	{
	        		rt = ResponseType.FAILURE;
	                message = "Not enough money.";
	        	}
        	}
        	else
        	{
        		double balance = adatok.getData().getDouble("players." + playerName + ".money");
	        	if(balance >= amount)
	        	{
	        		balance = balance - amount;
	        		adatok.getData().set("players." + playerName + ".money", balance);
	        		adatok.saveData();
	        		rt = ResponseType.SUCCESS;
	                message = null;
	        	}
	        	else
	        	{
	        		rt = ResponseType.FAILURE;
	                message = "Not enough money.";
	        	}
        	}
        }
        else
        {
        	Mysql.openConnection();
        	if(Mysql.MysqlRowGetDataBoolean("Bankcarduse", met.getPlayer(playerName)))
        	{
        		double balance = Mysql.MysqlRowGetDataDouble("Bank money", met.getPlayer(playerName));
	        	if(balance >= amount)
	        	{
	        		balance = balance - amount;
	        		Mysql.MysqlRowUpdateDataDouble("Bank money", balance, met.getPlayer(playerName));
	        		rt = ResponseType.SUCCESS;
	                message = null;
	                Mysql.closeConnection();
	        	}
	        	else
	        	{
	        		rt = ResponseType.FAILURE;
	                message = "Not enough money.";
	                Mysql.closeConnection();
	        	}
	        }
        	else
        	{
        		double balance = Mysql.MysqlRowGetDataDouble("Money", met.getPlayer(playerName));
	        	if(balance >= amount)
	        	{
	        		balance = balance - amount;
	        		Mysql.MysqlRowUpdateDataDouble("Money", balance, met.getPlayer(playerName));
	        		rt = ResponseType.SUCCESS;
	                message = null;
	                Mysql.closeConnection();
	        	}
	        	else
	        	{
	        		rt = ResponseType.FAILURE;
	                message = "Not enough money.";
	                Mysql.closeConnection();
	        	}
        	}
        }
        return new EconomyResponse(amount, getBalance(playerName), rt, message);
    }

   @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        ResponseType rt;
        String message;
        
        double balance = adatok.getData().getDouble("players." + playerName + ".money");
        if(!Mysql.mysql)
        {
	    	if(amount <= 0)
	    	{
	    		balance = balance + amount;
	    		adatok.getData().set("players." + playerName + ".money", balance);
	    		adatok.saveData();
	    		rt = ResponseType.SUCCESS;
	            message = null;
	    	}
	    	else
	    	{
	    		rt = ResponseType.FAILURE;
	            message = "Cant deposit negative funds.";
	    	}
        }
        else
        {
        	Mysql.openConnection();
        	balance = Mysql.MysqlRowGetDataDouble("Money", met.getPlayer(playerName));
        	if(amount <= 0)
	    	{
	    		balance = balance + amount;
	    		Mysql.MysqlRowUpdateDataDouble("Money", balance, met.getPlayer(playerName));
	    		rt = ResponseType.SUCCESS;
	            message = null;
	    	}
	    	else
	    	{
	    		rt = ResponseType.FAILURE;
	            message = "Cant deposit negative funds.";
	    	}
        	Mysql.closeConnection();
        }
        return new EconomyResponse(amount, getBalance(playerName), rt, message);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse deleteBank(String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineWallet does not support bank accounts.");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }
    /*@Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(playerName, amount);
    }*/
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }
    /*@Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(playerName, amount);
    }*/
    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }
    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

  public class EconomyServerListener implements Listener {
	  Economy_MineWallet economy = null;

    public EconomyServerListener(Economy_MineWallet economy) {
      this.economy = economy;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (economy.economy == null) {
        Plugin minewallet = event.getPlugin();

        if (minewallet.getDescription().getName().equals(economy.name)) {
          economy.economy = (MineWallet) minewallet;
          log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
      if (economy.economy != null) {
        if (event.getPlugin().getDescription().getName().equals(economy.name)) {
          economy.economy = null;
          log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
        }
      }
    }
  }
}