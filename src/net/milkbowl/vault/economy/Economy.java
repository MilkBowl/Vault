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

package net.milkbowl.vault.economy;

import java.util.List;

/**
 * The main economy API
 *
 */
public interface Economy {

    /**
     * Checks if economy method is enabled.
     * @return Success or Failure
     */
    public boolean isEnabled();

    /**
     * Gets name of economy method
     * @return Name of Ecoomy Method
     */
    public String getName();

    /**
     * Returns true if the given implementation supports banks.
     * @return true if the implementation supports banks
     */
    public boolean hasBankSupport();

    /**
     * Some economy plugins round off after a certain number of digits.
     * This function returns the number of digits the plugin keeps
     * or -1 if no rounding occurs.
     * @return number of digits after the decimal point kept
     */
    public int fractionalDigits();

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.  
     *
     * @param amount
     * @return Human readable string describing amount
     */
    public String format(double amount);

    /**
     * Returns the name of the currency in plural form.
     * If the economy being used does not support currency names then an empty string will be returned.
     * 
     * @return name of the currency (plural)
     */
    public String currencyNamePlural();


    /**
     * Returns the name of the currency in singular form.
     * If the economy being used does not support currency names then an empty string will be returned.
     * 
     * @return name of the currency (singular)
     */
    public String currencyNameSingular();

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     * @param playerName
     * @return if the player has an account
     */
    public boolean hasAccount(String playerName);

    /**
     * Checks if this player has an account on the server yet on the given world
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     * @param playerName
     * @return if the player has an account
     */
    public boolean hasAccount(String playerName, String worldName);

    /**
     * Gets balance of a player
     * @param playerName
     * @return Amount currently held in players account
     */
    public double getBalance(String playerName);
    
    /**
     * Gets balance of a player on the specified world.
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param playerName
     * @param world name of the world
     * @return Amount currently held in players account
     */
    public double getBalance(String playerName, String world);

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName
     * @param amount
     * @return True if <b>playerName</b> has <b>amount</b>, False else wise
     */
    public boolean has(String playerName, double amount);
    
    /**
     * Checks if the player account has the amount in a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param playerName
     * @param worldName
     * @param amount
     * @return True if <b>playerName</b> has <b>amount</b>, False else wise
     */
    public boolean has(String playerName, String worldName, double amount);
    
    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName Name of player
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(String playerName, double amount);

    /**
     * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param playerName Name of player
     * @param worldName - name of the world
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount);
    
    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName Name of player
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    public EconomyResponse depositPlayer(String playerName, double amount);

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param playerName Name of player
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount);
   
    /**
     * Creates a bank account with the specified name and the player as the owner
     * @param name
     * @param player
     * @return EconomyResponse Object
     */
    public EconomyResponse createBank(String name, String player);

    /**
     * Deletes a bank account with the specified name.
     * @param name
     * @return if the operation completed successfully
     */
    public EconomyResponse deleteBank(String name);

    /**
     * Returns the amount the bank has
     * @param name
     * @return EconomyResponse Object
     */
    public EconomyResponse bankBalance(String name);

    /**
     * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name
     * @param amount
     * @return EconomyResponse Object
     */
    public EconomyResponse bankHas(String name, double amount);

    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name
     * @param amount
     * @return EconomyResponse Object
     */
    public EconomyResponse bankWithdraw(String name, double amount);

    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name
     * @param amount
     * @return EconomyResponse Object
     */
    public EconomyResponse bankDeposit(String name, double amount);

    /**
     * Check if a player is the owner of a bank account
     * @param name
     * @param playerName
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankOwner(String name, String playerName);

    /**
     * Check if the player is a member of the bank account
     * @param name
     * @param playerName
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankMember(String name, String playerName);

    /**
     * Gets the list of banks
     * @return the List of Banks
     */
    public List<String> getBanks();

    /**
     * Attempts to create a player account for the given player
     * @return if the account creation was successful
     */
    public boolean createPlayerAccount(String playerName);
    
    /**
     * Attempts to create a player account for the given player on the specified world
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @return if the account creation was successful
     */
    public boolean createPlayerAccount(String playerName, String worldName);
    
    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerName Name of player
     * @param amount Amount to withdraw
     * @param sendMessage whether or not to send the player a message when the money is taken.
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(String playerName, double amount, boolean sendMessage);
   
	/**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param playerName Name of player
     * @param amount Amount to deposit
     * @param sendMessage whether or not to send the player a message when the money is taken.
     * @return Detailed response of transaction
     */
	public EconomyResponse depositPlayer(String playerName, double amount, boolean sendMessage);
}

