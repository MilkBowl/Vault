/**
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
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
     * Gets name of permission method
     * @return Name of Permission Method
     */
    public String getName();

    /**
     * Returns true if the given implementation supports banks.
     * @return true if the implementation supports banks
     */
    public abstract boolean hasBankSupport();

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     * Should be used when possible.
     * @param amount
     * @return Human readable string describing amount
     */
    public String format(double amount);

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     * @param playerName
     * @return if the player has an account
     */
    public boolean hasAccount(String playerName);


    /**
     * Gets balance of a player
     * @param playerName
     * @return Amount currently held in players account
     */
    public double getBalance(String playerName);

    /**
     * Checks if the player account has the amount
     * @param playerName
     * @param amount
     * @return
     */
    public boolean has(String playerName, double amount);
    /**
     * Withdraw an amount from a player
     * @param playerName Name of player
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(String playerName, double amount);

    /**
     * Deposit an amount to a player
     * @param playerName Name of player
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    public EconomyResponse depositPlayer(String playerName, double amount);

    /**
     * Creates a bank account with the specified name and the player as the owner
     * @param name
     * @param player
     * @return
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
     * @return
     */
    public EconomyResponse bankBalance(String name);

    /**
     * Returns true or false whether the bank has the amount specified
     * @param name
     * @param amount
     * @return
     */
    public EconomyResponse bankHas(String name, double amount);

    /**
     * Withdraw an amount from a bank account
     * @param name
     * @param amount
     * @return
     */
    public EconomyResponse bankWithdraw(String name, double amount);

    /**
     * Deposit an amount into a bank account
     * @param name
     * @param amount
     * @return
     */
    public EconomyResponse bankDeposit(String name, double amount);

    /**
     * Check if a player is the owner of a bank account
     * @param name
     * @param playerName
     * @return
     */
    public EconomyResponse isBankOwner(String name, String playerName);

    /**
     * Check if the player is a member of the bank account
     * @param name
     * @param playerName
     * @return
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
}