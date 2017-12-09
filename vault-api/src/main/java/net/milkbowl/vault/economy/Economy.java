/*
 * This file is part of Vault.
 *
 * Copyright (c) 2011 Morgan Humes <morgan@lanaddict.com>
 * Copyright (c) 2017 Neolumia
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.milkbowl.vault.economy;

import java.util.List;
import org.bukkit.OfflinePlayer;

/**
 * The main Economy API.
 */
public interface Economy {

  /**
   * Checks if economy method is enabled.
   *
   * @return Success or Failure
   */
  boolean isEnabled();

  /**
   * Gets name of economy method.
   *
   * @return Name of Economy Method
   */
  String getName();

  /**
   * Returns true if the given implementation supports banks.
   *
   * @return true if the implementation supports banks
   */
  boolean hasBankSupport();

  /**
   * Some economy plugins round off after a certain number of digits.
   * This function returns the number of digits the plugin keeps
   * or -1 if no rounding occurs.
   *
   * @return number of digits after the decimal point kept
   */
  int fractionalDigits();

  /**
   * Format amount into a human readable String This provides translation into
   * economy specific formatting to improve consistency between plugins.
   *
   * @param amount to format
   * @return Human readable string describing amount
   */
  String format(double amount);

  /**
   * Returns the name of the currency in plural form.
   * If the economy being used does not support currency names then an empty string will be
   * returned.
   *
   * @return name of the currency (plural)
   */
  String currencyNamePlural();

  /**
   * Returns the name of the currency in singular form.
   * If the economy being used does not support currency names then an empty string will be
   * returned.
   *
   * @return name of the currency (singular)
   */
  String currencyNameSingular();

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)} instead.
   */
  @Deprecated
  boolean hasAccount(String playerName);

  /**
   * Checks if this player has an account on the server yet.
   * This will always return true if the player has joined the server at least once
   * as all major economy plugins auto-generate a player account when the player joins the server.
   *
   * @param player to check
   * @return if the player has an account
   */
  boolean hasAccount(OfflinePlayer player);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)} instead.
   */
  @Deprecated
  boolean hasAccount(String playerName, String worldName);

  /**
   * Checks if this player has an account on the server yet on the given world.
   * This will always return true if the player has joined the server at least once
   * as all major economy plugins auto-generate a player account when the player joins the server.
   *
   * @param player    to check in the world
   * @param worldName world-specific account
   * @return if the player has an account
   */
  boolean hasAccount(OfflinePlayer player, String worldName);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)} instead.
   */
  @Deprecated
  double getBalance(String playerName);

  /**
   * Gets balance of a player.
   *
   * @param player of the player
   * @return Amount currently held in players account
   */
  double getBalance(OfflinePlayer player);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)} instead.
   */
  @Deprecated
  double getBalance(String playerName, String world);

  /**
   * Gets balance of a player on the specified world.
   * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will
   * be returned.
   *
   * @param player to check
   * @param world  name of the world
   * @return Amount currently held in players account
   */
  double getBalance(OfflinePlayer player, String world);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)} instead.
   */
  @Deprecated
  boolean has(String playerName, double amount);

  /**
   * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS.
   *
   * @param player to check
   * @param amount to check for
   * @return True if <b>player</b> has <b>amount</b>, False else wise
   */
  boolean has(OfflinePlayer player, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use @{link {@link #has(OfflinePlayer, String, double)} instead.
   */
  @Deprecated
  boolean has(String playerName, String worldName, double amount);

  /**
   * Checks if the player account has the amount in a given world - DO NOT USE NEGATIVE AMOUNTS
   * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance
   * will be returned.
   *
   * @param player    to check
   * @param worldName to check with
   * @param amount    to check for
   * @return True if <b>player</b> has <b>amount</b>, False else wise
   */
  boolean has(OfflinePlayer player, String worldName, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, double)} instead.
   */
  @Deprecated
  EconomyResponse withdrawPlayer(String playerName, double amount);

  /**
   * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS.
   *
   * @param player to withdraw from
   * @param amount Amount to withdraw
   * @return Detailed response of transaction
   */
  EconomyResponse withdrawPlayer(OfflinePlayer player, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, String, double)}
   * instead.
   */
  @Deprecated
  EconomyResponse withdrawPlayer(String playerName, String worldName, double amount);

  /**
   * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE AMOUNTS
   * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will
   * be returned.
   *
   * @param player    to withdraw from
   * @param worldName - name of the world
   * @param amount    Amount to withdraw
   * @return Detailed response of transaction
   */
  EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, double)} instead.
   */
  @Deprecated
  EconomyResponse depositPlayer(String playerName, double amount);

  /**
   * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS.
   *
   * @param player to deposit to
   * @param amount Amount to deposit
   * @return Detailed response of transaction
   */
  EconomyResponse depositPlayer(OfflinePlayer player, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, String, double)}
   * instead.
   */
  @Deprecated
  EconomyResponse depositPlayer(String playerName, String worldName, double amount);

  /**
   * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS.
   * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will
   * be returned.
   *
   * @param player    to deposit to
   * @param worldName name of the world
   * @param amount    Amount to deposit
   * @return Detailed response of transaction
   */
  EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use {{@link #createBank(String, OfflinePlayer)} instead.
   */
  @Deprecated
  EconomyResponse createBank(String name, String player);

  /**
   * Creates a bank account with the specified name and the player as the owner.
   *
   * @param name   of account
   * @param player the account should be linked to
   * @return EconomyResponse Object
   */
  EconomyResponse createBank(String name, OfflinePlayer player);

  /**
   * Deletes a bank account with the specified name.
   *
   * @param name of the back to delete
   * @return if the operation completed successfully
   */
  EconomyResponse deleteBank(String name);

  /**
   * Returns the amount the bank has.
   *
   * @param name of the account
   * @return EconomyResponse Object
   */
  EconomyResponse bankBalance(String name);

  /**
   * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS.
   *
   * @param name   of the account
   * @param amount to check for
   * @return EconomyResponse Object
   */
  EconomyResponse bankHas(String name, double amount);

  /**
   * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS.
   *
   * @param name   of the account
   * @param amount to withdraw
   * @return EconomyResponse Object
   */
  EconomyResponse bankWithdraw(String name, double amount);

  /**
   * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS.
   *
   * @param name   of the account
   * @param amount to deposit
   * @return EconomyResponse Object
   */
  EconomyResponse bankDeposit(String name, double amount);

  /**
   * @deprecated As of VaultAPI 1.4 use {{@link #isBankOwner(String, OfflinePlayer)} instead.
   */
  @Deprecated
  EconomyResponse isBankOwner(String name, String playerName);

  /**
   * Check if a player is the owner of a bank account.
   *
   * @param name   of the account
   * @param player to check for ownership
   * @return EconomyResponse Object
   */
  EconomyResponse isBankOwner(String name, OfflinePlayer player);

  /**
   * @deprecated As of VaultAPI 1.4 use {{@link #isBankMember(String, OfflinePlayer)} instead.
   */
  @Deprecated
  EconomyResponse isBankMember(String name, String playerName);

  /**
   * Check if the player is a member of the bank account.
   *
   * @param name   of the account
   * @param player to check membership
   * @return EconomyResponse Object
   */
  EconomyResponse isBankMember(String name, OfflinePlayer player);

  /**
   * Gets the list of banks.
   *
   * @return the List of Banks
   */
  List<String> getBanks();

  /**
   * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer)} instead.
   */
  @Deprecated
  boolean createPlayerAccount(String playerName);

  /**
   * Attempts to create a player account for the given player.
   *
   * @param player OfflinePlayer
   * @return if the account creation was successful
   */
  boolean createPlayerAccount(OfflinePlayer player);

  /**
   * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  boolean createPlayerAccount(String playerName, String worldName);

  /**
   * Attempts to create a player account for the given player on the specified world.
   * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will
   * be returned.
   *
   * @param player    OfflinePlayer
   * @param worldName String name of the world
   * @return if the account creation was successful
   */
  boolean createPlayerAccount(OfflinePlayer player, String worldName);
}
