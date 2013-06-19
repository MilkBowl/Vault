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

import org.vanillaworld.SimpleEconomy.Functions;
import org.vanillaworld.SimpleEconomy.Backend;
import org.vanillaworld.SimpleEconomy.Main;

public class Economy_SimpleEconomy implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "SimpleEconomy";
    private Plugin plugin = null;
    protected Main economy = null;

    public Economy_SimpleEconomy(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("SimpleEconomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("org.vanillaworld.SimpleEconomy.Main")) {
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (economy == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String format(double amount) {
        return Functions.format(amount);
    }

    @Override
    public String currencyNameSingular() {
        return Functions.currencyName();
    }

    @Override
    public String currencyNamePlural() {
        return Functions.currencyName();
    }

    @Override
    public double getBalance(String playerName) {
        return (double) Backend.getMoney(playerName);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        if (Backend.hasEnoughMoney(playerName, amount)) {
            int newAmount = Backend.getMoney(playerName) - amount;
            Backend.setMoney(playerName, newAmount);
            return new EconomyResponse(amount, newAmount, ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, Backend.getMoney(playerName), ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        int newAmount = Backend.getMoney(playerName) + amount;
        Backend.setMoney(playerName, newAmount);
        return new EconomyResponse(amount, newAmount, ResponseType.SUCCESS, null);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        
       return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
       return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Banks not supported by SimpleEconomy!");
    }

    @Override
    public List<String> getBanks() {
        throw new UnsupportedOperationException("Banks not supported by SimpleEconomy!");
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return true; // All players have an account
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true; // All players have an account
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
}
