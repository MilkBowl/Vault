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

package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import me.ethan.eWallet.ECO;
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

public class Economy_eWallet extends AbstractEconomy {
  private static final Logger log = Logger.getLogger("Minecraft");

  private final String name = "eWallet";
  private Plugin plugin = null;
  private ECO econ = null;

  public Economy_eWallet(Plugin plugin) {
    this.plugin = plugin;
    Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

    // Load Plugin in case it was loaded before
    if (econ == null) {
      Plugin econ = plugin.getServer().getPluginManager().getPlugin("eWallet");
      if (econ != null && econ.isEnabled()) {
        this.econ = (ECO) econ;
        log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
      }
    }
  }

  @Override
  public boolean isEnabled() {
    return this.econ != null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String format(double amount) {
    amount = Math.ceil(amount);
    if (amount == 1) {
      return String.format("%d %s", (int) amount, econ.singularCurrency);
    } else {
      return String.format("%d %s", (int) amount, econ.pluralCurrency);
    }
  }

  @Override
  public String currencyNameSingular() {
    return econ.singularCurrency;
  }

  @Override
  public String currencyNamePlural() {
    return econ.pluralCurrency;
  }

  @Override
  public double getBalance(String playerName) {
    Integer i = econ.getMoney(playerName);
    return i == null ? 0 : i;
  }

  @Override
  public boolean has(String playerName, double amount) {
    return getBalance(playerName) >= Math.ceil(amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(String playerName, double amount) {
    double balance = getBalance(playerName);
    amount = Math.ceil(amount);
    if (amount < 0) {
      return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
    } else if (balance >= amount) {
      double finalBalance = balance - amount;
      econ.takeMoney(playerName, (int) amount);
      return new EconomyResponse(amount, finalBalance, ResponseType.SUCCESS, null);
    } else {
      return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds");
    }
  }

  @Override
  public EconomyResponse depositPlayer(String playerName, double amount) {
    double balance = getBalance(playerName);
    amount = Math.ceil(amount);
    if (amount < 0) {
      return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot deposit negative funds");
    } else {
      balance += amount;
      econ.giveMoney(playerName, (int) amount);
      return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
    }
  }

  @Override
  public EconomyResponse createBank(String name, String player) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse deleteBank(String name) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse bankHas(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse bankWithdraw(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse bankDeposit(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse isBankOwner(String name, String playerName) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse isBankMember(String name, String playerName) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public EconomyResponse bankBalance(String name) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
  }

  @Override
  public List<String> getBanks() {
    return new ArrayList<String>();
  }

  @Override
  public boolean hasBankSupport() {
    return false;
  }

  @Override
  public boolean hasAccount(String playerName) {
    return econ.hasAccount(playerName);
  }

  @Override
  public boolean createPlayerAccount(String playerName) {
    if (hasAccount(playerName)) {
      return false;
    }
    econ.createAccount(playerName, 0);
    return true;
  }

  @Override
  public int fractionalDigits() {
    return 0;
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
    Economy_eWallet economy = null;

    public EconomyServerListener(Economy_eWallet economy) {
      this.economy = economy;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (economy.econ == null) {
        Plugin eco = event.getPlugin();

        if (eco.getDescription().getName().equals("eWallet")) {
          economy.econ = (ECO) eco;
          log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
      if (economy.econ != null) {
        if (event.getPlugin().getDescription().getName().equals("eWallet")) {
          economy.econ = null;
          log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
        }
      }
    }
  }
}
