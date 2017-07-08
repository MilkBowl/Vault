/*
 * This file is part of Vault.
 *
 * Copyright (C) 2017 Lukas Nehrke
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
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

package net.milkbowl.vault.economy.plugins;

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
import org.gestern.gringotts.Account;
import org.gestern.gringotts.AccountHolder;
import org.gestern.gringotts.Gringotts;

public class Economy_Gringotts extends AbstractEconomy {

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

    @Override
    public boolean isEnabled(){
        return gringotts != null && gringotts.isEnabled();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport(){
        return false;
    }

    @Override
    public int fractionalDigits(){
        return 2;
    }

    @Override
    public String format(double amount) {
        return Double.toString(amount);
    }

    @Override
    public String currencyNamePlural(){
        return org.gestern.gringotts.Configuration.config.currencyNamePlural;
    }

    @Override
    public String currencyNameSingular(){
        return org.gestern.gringotts.Configuration.config.currencyNameSingular;
    }

    @Override
    public boolean hasAccount(String playerName) {
        AccountHolder owner = gringotts.accountHolderFactory.getAccount(playerName);
        if (owner == null) {
            return false;
        }

        return gringotts.accounting.getAccount(owner) != null;
    }

    @Override
    public double getBalance(String playerName){
        AccountHolder owner = gringotts.accountHolderFactory.getAccount(playerName);
        if (owner == null) {
            return 0;
        }
        Account account = gringotts.accounting.getAccount(owner);
        return account.balance();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

        if( amount < 0 ) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount.");
        }

        AccountHolder accountHolder = gringotts.accountHolderFactory.getAccount(playerName);
        if (accountHolder == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, playerName + " is not a valid account holder.");
        }

        Account account = gringotts.accounting.getAccount( accountHolder );

        if(account.balance() >= amount && account.remove(amount)) {
            //We has mulah!
            return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
        } else {
            //Not enough money to withdraw this much.
            return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Insufficient funds");
        }

    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount){
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        AccountHolder accountHolder = gringotts.accountHolderFactory.getAccount(playerName);
        if (accountHolder == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, playerName + " is not a valid account holder.");
        }

        Account account = gringotts.accounting.getAccount( accountHolder );

      if (account.add(amount)) {
            return new EconomyResponse( amount, account.balance(), ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse( 0, account.balance(), ResponseType.FAILURE, "Not enough capacity to store that amount!");
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

  public class EconomyServerListener implements Listener {
    Economy_Gringotts economy = null;

    public EconomyServerListener(Economy_Gringotts economy_Gringotts) {
      this.economy = economy_Gringotts;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (economy.gringotts == null) {
        Plugin grngts = event.getPlugin();

        if (grngts.getDescription().getName().equals("Gringotts")) {
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
}
