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

import co.uk.silvania.cities.digicoin.DigiCoin;
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

public class Economy_DigiCoin extends AbstractEconomy {
  private static final Logger log = Logger.getLogger("Minecraft");

  private final String name = "DigiCoin";
  private Plugin plugin = null;
  private DigiCoin economy = null;

  public Economy_DigiCoin(Plugin plugin) {
    this.plugin = plugin;
    Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

    if (economy == null) {
      Plugin digicoin = plugin.getServer().getPluginManager().getPlugin(name);

      if (digicoin != null && digicoin.isEnabled()) {
        economy = (DigiCoin) digicoin;
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
  public String format(double amount) {
    if (amount == 1.0) {
      return String.format("%d %s", amount, currencyNameSingular());
    } else {
      return String.format("%d %s", amount, currencyNamePlural());
    }
  }

  @Override
  public String currencyNamePlural() {
    return "coins";
  }

  @Override
  public String currencyNameSingular() {
    return "coin";
  }

  @Override
  public boolean hasAccount(String playerName) {
    return true;
  }

  @Override
  public double getBalance(String playerName) {
    return economy.getBalance(playerName);
  }

  @Override
  public boolean has(String playerName, double amount) {
    return getBalance(playerName) >= amount;
  }

  @Override
  public EconomyResponse withdrawPlayer(String playerName, double amount) {
    ResponseType rt;
    String message;

    if (economy.removeBalance(playerName, amount)) {
      rt = ResponseType.SUCCESS;
      message = null;
    } else {
      rt = ResponseType.FAILURE;
      message = "Not enough money.";
    }

    return new EconomyResponse(amount, getBalance(playerName), rt, message);
  }

  @Override
  public EconomyResponse depositPlayer(String playerName, double amount) {
    ResponseType rt;
    String message;

    if (economy.addBalance(playerName, amount)) {
      rt = ResponseType.SUCCESS;
      message = null;
    } else {
      rt = ResponseType.FAILURE;
      message = "Failed to deposit balance.";
    }

    return new EconomyResponse(amount, getBalance(playerName), rt, message);
  }

  @Override
  public EconomyResponse createBank(String name, String player) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse deleteBank(String name) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse bankBalance(String name) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse bankHas(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse bankWithdraw(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse bankDeposit(String name, double amount) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse isBankOwner(String name, String playerName) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
  }

  @Override
  public EconomyResponse isBankMember(String name, String playerName) {
    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
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
    return false;
  }

  public class EconomyServerListener implements Listener {
    Economy_DigiCoin economy = null;

    public EconomyServerListener(Economy_DigiCoin economy) {
      this.economy = economy;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (economy.economy == null) {
        Plugin digicoin = event.getPlugin();

        if (digicoin.getDescription().getName().equals(economy.name)) {
          economy.economy = (DigiCoin) digicoin;
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
