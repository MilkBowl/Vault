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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import me.igwb.GoldenChest.GoldenChestEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_GoldenChestEconomy implements Economy{
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private final String name = "GoldenChestEconomy";
    private Plugin plugin = null;
    private GoldenChestEconomy economy = null;
    
    
    public Economy_GoldenChestEconomy (Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldIsMoney");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.igwb.GoldenChest.GoldenChestEconomy")) {
                economy = (GoldenChestEconomy) ec;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }
    
    public class EconomyServerListener implements Listener {
        Economy_GoldenChestEconomy economy = null;
    
        public EconomyServerListener(Economy_GoldenChestEconomy economy_GoldenChestEconomy) {
            this.economy = economy_GoldenChestEconomy;
        }
    
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin ec = event.getPlugin();
    
                if (ec.getDescription().getName().equals("GoldenChestEconomy") && ec.getClass().getName().equals("me.igwb.GoldenChest.GoldenChestEconomy")) {
                    economy.economy = (GoldenChestEconomy) ec;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("GoldenChestEconomy")) {
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
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return economy.getVaultConnector().fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return economy.getVaultConnector().format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return economy.getVaultConnector().currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return economy.getVaultConnector().currencyNameSingular();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return economy.getVaultConnector().hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return economy.getVaultConnector().hasAccount(playerName, worldName);
    }

    @Override
    public double getBalance(String playerName) {
        return economy.getVaultConnector().getBalance(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return economy.getVaultConnector().getBalance(playerName, world);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return economy.getVaultConnector().has(playerName, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return economy.getVaultConnector().has(playerName, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }
    
        if (has(playerName, amount)) {
            economy.getVaultConnector().withdrawPlayer(playerName, amount);
            return new EconomyResponse(amount, getBalance(playerName), ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, getBalance(playerName), ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName,
            double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }
        
        economy.getVaultConnector().depositPlayer(playerName, Math.round(amount));
        return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName,
            double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return economy.getVaultConnector().createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return economy.getVaultConnector().createPlayerAccount(playerName, worldName);
    }

}
