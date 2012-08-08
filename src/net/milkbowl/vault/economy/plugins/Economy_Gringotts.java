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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import org.gestern.gringotts.Account;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.PlayerAccountHolder;

public class Economy_Gringotts implements Economy {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "Gringotts";
    private Plugin plugin = null;
    private Gringotts gringotts = null;
	
    public Economy_Gringotts(Plugin plugin) {
    	this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        // Load Plugin in case it was loaded before
        if (gringotts == null) {
            Plugin grngts = plugin.getServer().getPluginManager().getPlugin("Gringotts");
            if (grngts != null && grngts.isEnabled()) {
            	gringotts = (Gringotts) grngts;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }
    
    public class EconomyServerListener implements Listener {
        Economy_Gringotts economy = null;

        public EconomyServerListener(Economy_Gringotts economy_Gringotts) {
            this.economy = economy_Gringotts;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.gringotts == null) {
                Plugin grngts = plugin.getServer().getPluginManager().getPlugin("Gringotts");

                if (grngts != null && grngts.isEnabled()) {
                    economy.gringotts = (Gringotts) grngts;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.gringotts != null) {
                if (event.getPlugin().getDescription().getName().equals("Gringotts")) {
                    economy.gringotts = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }
    
    /**
     * Checks if economy method is enabled.
     * @return Success or Failure
     */
    public boolean isEnabled(){
        if (gringotts == null) {
            return false;
        } else {
            return gringotts.isEnabled();
        }
    }

    /**
     * Gets name of permission method
     * @return Name of Permission Method
     */
    public String getName() {
		return name;
	}

    /**
     * Returns true if the given implementation supports banks.
     * @return true if the implementation supports banks
     */
    public boolean hasBankSupport(){
    	return false;
    }

    /**
     * Some economy plugins round off after a certain number of digits.
     * This function returns the number of digits the plugin keeps
     * or -1 if no rounding occurs.
     * @return number of digits after the decimal point kept
     */
    public int fractionalDigits(){
    	return 2;
    }

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.  
     *
     * @param amount
     * @return Human readable string describing amount
     */
    public String format(double amount) {
    	return Double.toString(amount);
    }

    /**
     * Returns the name of the currency in plural form.
     * If the economy being used does not support currency names then an empty string will be returned.
     * 
     * @return name of the currency (plural)
     */
    public String currencyNamePlural(){
    	return org.gestern.gringotts.Configuration.config.currencyNamePlural;
    }


    /**
     * Returns the name of the currency in singular form.
     * If the economy being used does not support currency names then an empty string will be returned.
     * 
     * @return name of the currency (singular)
     */
    public String currencyNameSingular(){
    	return org.gestern.gringotts.Configuration.config.currencyNameSingular;
    }

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     * @param playerName
     * @return if the player has an account
     */
    public boolean hasAccount(String playerName) {
    	try{
    		Account account = gringotts.accounting.getAccount(new PlayerAccountHolder(playerName));
    		if(account != null)
    			return true;
    		else
    			return false;
    	}
    	catch (Exception e){
    		return false;
    	}
	}


    /**
     * Gets balance of a player
     * @param playerName
     * @return Amount currently held in players account
     */
    public double getBalance(String playerName){
        Account account = gringotts.accounting.getAccount(new PlayerAccountHolder(playerName));
		return account.balance();
    }

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName
     * @param amount
     * @return
     * @throws UserDoesNotExistException 
     */
    public boolean has(String playerName, double amount) {
    	return getBalance(playerName) >= amount;
    }
    
    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName Name of player
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        
        if( amount < 0 ) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount.");
        }
        
        PlayerAccountHolder accountHolder = new PlayerAccountHolder(playerName);
        
        Account account = gringotts.accounting.getAccount( accountHolder );
        
        if(account.balance() >= amount && account.remove(amount)) {
            //We has mulah!
            return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
        } else {
            //Not enough money to withdraw this much.
            return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Insufficient funds");
        }
        
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName Name of player
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    public EconomyResponse depositPlayer(String playerName, double amount){
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }
        
        PlayerAccountHolder accountHolder = new PlayerAccountHolder(playerName);
        Account account = gringotts.accounting.getAccount( accountHolder );
        
        if (account.add(amount))        
        	return new EconomyResponse( amount, account.balance(), ResponseType.SUCCESS, null);
        else
        	return new EconomyResponse( 0, account.balance(), ResponseType.FAILURE, "Not enough capacity to store that amount!");
        
    }

    /**
     * Creates a bank account with the specified name and the player as the owner
     * @param name
     * @param player
     * @return
     */
    public EconomyResponse createBank(String name, String player) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Deletes a bank account with the specified name.
     * @param name
     * @return if the operation completed successfully
     */
    public EconomyResponse deleteBank(String name) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Returns the amount the bank has
     * @param name
     * @return
     */
    public EconomyResponse bankBalance(String name) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name
     * @param amount
     * @return
     */
    public EconomyResponse bankHas(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name
     * @param amount
     * @return
     */
    public EconomyResponse bankWithdraw(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name
     * @param amount
     * @return
     */
    public EconomyResponse bankDeposit(String name, double amount) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Check if a player is the owner of a bank account
     * @param name
     * @param playerName
     * @return
     */
    public EconomyResponse isBankOwner(String name, String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Check if the player is a member of the bank account
     * @param name
     * @param playerName
     * @return
     */
    public EconomyResponse isBankMember(String name, String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    /**
     * Gets the list of banks
     * @return the List of Banks
     */
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    /**
     * Attempts to create a player account for the given player
     * @return if the account creation was successful
     */
    public boolean createPlayerAccount(String playerName) {
        if (hasAccount(playerName)) {
            return false;
        }
        else
        	return true;
        
        
    }
}
