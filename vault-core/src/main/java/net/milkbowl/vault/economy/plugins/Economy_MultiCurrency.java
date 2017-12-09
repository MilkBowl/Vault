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
import me.ashtheking.currency.Currency;
import me.ashtheking.currency.CurrencyList;
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

public class Economy_MultiCurrency extends AbstractEconomy {
  private static final Logger log = Logger.getLogger("Minecraft");

  private final String name = "MultiCurrency";
  private Plugin plugin = null;
  private Currency economy = null;

  public Economy_MultiCurrency(Plugin plugin) {
    this.plugin = plugin;
    Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

    // Load Plugin in case it was loaded before
    if (economy == null) {
      Plugin multiCurrency = plugin.getServer().getPluginManager().getPlugin("MultiCurrency");
      if (multiCurrency != null && multiCurrency.isEnabled()) {
        economy = (Currency) multiCurrency;
        log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
      }
    }
  }

  @Override
  public String getName() {
    return name;
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
  public double getBalance(String playerName) {
    final double balance;

    balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

    final double fBalance = balance;
    return fBalance;
  }

  @Override
  public EconomyResponse withdrawPlayer(String playerName, double amount) {
    double balance;
    ResponseType type;
    String errorMessage = null;

    if (amount < 0) {
      errorMessage = "Cannot withdraw negative funds";
      type = ResponseType.FAILURE;
      amount = 0;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    }

    if (!CurrencyList.hasEnough(playerName, amount)) {
      errorMessage = "Insufficient funds";
      type = ResponseType.FAILURE;
      amount = 0;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    }

    if (CurrencyList.subtract(playerName, amount)) {
      type = ResponseType.SUCCESS;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    } else {
      errorMessage = "Error withdrawing funds";
      type = ResponseType.FAILURE;
      amount = 0;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    }
  }

  @Override
  public EconomyResponse depositPlayer(String playerName, double amount) {
    double balance;
    ResponseType type;
    String errorMessage = null;

    if (amount < 0) {
      errorMessage = "Cannot deposit negative funds";
      type = ResponseType.FAILURE;
      amount = 0;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    }

    if (CurrencyList.add(playerName, amount)) {
      type = ResponseType.SUCCESS;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    } else {
      errorMessage = "Error withdrawing funds";
      type = ResponseType.FAILURE;
      amount = 0;
      balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);

      return new EconomyResponse(amount, balance, type, errorMessage);
    }
  }

  @Override
  public String format(double amount) {
    return String.format("%.2f %s", amount, "currency");
  }

  @Override
  public String currencyNameSingular() {
    return "currency";
  }

  @Override
  public String currencyNamePlural() {
    return "currency";
  }

  @Override
  public boolean has(String playerName, double amount) {
    return getBalance(playerName) >= amount;
  }

  @Override
  public EconomyResponse createBank(String name, String player) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
  }

  @Override
  public EconomyResponse deleteBank(String name) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts!");
  }

  @Override
  public EconomyResponse bankHas(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
  }

  @Override
  public EconomyResponse bankWithdraw(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
  }

  @Override
  public EconomyResponse bankDeposit(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
  }

  @Override
  public EconomyResponse isBankOwner(String name, String playerName) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
  }

  @Override
  public EconomyResponse isBankMember(String name, String playerName) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
  }

  @Override
  public EconomyResponse bankBalance(String name) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
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
    return true;
  }

  @Override
  public boolean createPlayerAccount(String playerName) {
    return false;
  }

  @Override
  public int fractionalDigits() {
    return -1;
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
    Economy_MultiCurrency economy = null;

    public EconomyServerListener(Economy_MultiCurrency economy) {
      this.economy = economy;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (economy.economy == null) {
        Plugin mcur = event.getPlugin();

        if (mcur.getDescription().getName().equals("MultiCurrency")) {
          economy.economy = (Currency) mcur;
          log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
      if (economy.economy != null) {
        if (event.getPlugin().getDescription().getName().equals("MultiCurrency")) {
          economy.economy = null;
          log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
        }
      }
    }
  }
}
