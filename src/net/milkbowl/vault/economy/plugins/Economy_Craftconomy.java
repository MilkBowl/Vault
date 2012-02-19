/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault.economy.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import me.greatman.Craftconomy.Account;
import me.greatman.Craftconomy.AccountHandler;
import me.greatman.Craftconomy.Bank;
import me.greatman.Craftconomy.BankHandler;
import me.greatman.Craftconomy.Craftconomy;
import me.greatman.Craftconomy.CurrencyHandler;
import me.greatman.Craftconomy.utils.Config;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Economy_Craftconomy implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "Craftconomy";
    private JavaPlugin plugin = null;
    protected Craftconomy economy = null;

    public Economy_Craftconomy(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.greatman.Craftconomy.Craftconomy")) {
                economy = (Craftconomy) ec;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_Craftconomy economy = null;

        public EconomyServerListener(Economy_Craftconomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy");

                if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.greatman.Craftconomy.Craftconomy")) {
                    economy.economy = (Craftconomy) ec;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Craftconomy")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
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
    public String format(double amount) {
        return Craftconomy.format(amount, CurrencyHandler.getCurrency(Config.currencyDefault, true));
    }

    @Override
    public double getBalance(String playerName) {
        if (AccountHandler.exists(playerName)) {
            return AccountHandler.getAccount(playerName).getDefaultBalance();
        } else {
            return 0;
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        Account account = AccountHandler.getAccount(playerName);
        if (account.hasEnough(amount)) {
            balance = account.substractMoney(amount);
            return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, account.getDefaultBalance(), ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Account account = AccountHandler.getAccount(playerName);
        account.addMoney(amount);
        return new EconomyResponse(amount, account.getDefaultBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
    	boolean success = BankHandler.create(name, player);
        if (success) {
            return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
        }
        
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create that bank account.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
    	boolean success = BankHandler.delete(name);
    	if (success) {
    		return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
    	}
    	        
    	return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create that bank account.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
    	
    	if (BankHandler.exists(name))
    	{
    		Bank bank = BankHandler.getBank(name);
    		if (bank.hasEnough(amount))
    			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
    		else
    			return new EconomyResponse(0, bank.getDefaultBalance(), ResponseType.FAILURE, "The bank does not have enough money!");
    	}
    	return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
    	EconomyResponse er = bankHas(name, amount);
        if (!er.transactionSuccess())
            return er;
        else
        {
        	if (BankHandler.exists(name))
        	{
        		Bank bank = BankHandler.getBank(name);
        		double balance = bank.substractMoney(amount);
        		return new EconomyResponse(0, balance, ResponseType.SUCCESS, "");
        	}
        	return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
    	
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
    	if (BankHandler.exists(name))
    	{
    		Bank bank = BankHandler.getBank(name);
    		double balance = bank.addMoney(amount);
    		return new EconomyResponse(0, balance, ResponseType.SUCCESS, "");
    	}
    	return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        if (BankHandler.exists(name))
        {
        	Bank bank = BankHandler.getBank(name);
        	if (bank.getOwner().equals(playerName))
        	{
        		return new EconomyResponse(0, bank.getDefaultBalance(), ResponseType.SUCCESS, "");
        	}
        	return new EconomyResponse(0, 0, ResponseType.FAILURE, "This player is not the owner of the bank!");
        }
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
    	EconomyResponse er = isBankOwner(name,playerName);
    	if (er.transactionSuccess())
    		return er;
    	else
    	{
    		if (BankHandler.exists(name))
    		{
    			Bank bank = BankHandler.getBank(name);
    			Iterator<String> iterator = bank.getMembers().iterator();
    			while(iterator.hasNext())
    			{
    				if (iterator.next().equals(playerName))
    					return new EconomyResponse(0,bank.getDefaultBalance(), ResponseType.SUCCESS, "");
    			}
    			
    		}
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "This player is not a member of the bank!");
    	}
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if (BankHandler.exists(name))
        {
        	return new EconomyResponse(0, BankHandler.getBank(name).getDefaultBalance(), ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
    }

    @Override
    public List<String> getBanks() {
        return BankHandler.listBanks();
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return AccountHandler.exists(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (AccountHandler.exists(playerName)) {
            return false;
        }
        AccountHandler.getAccount(playerName);
        return true;
    }
}