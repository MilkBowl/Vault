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

import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.github.omwah.SDFEconomy.SDFEconomy;
import com.github.omwah.SDFEconomy.SDFEconomyAPI;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Economy_SDFEconomy implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Plugin plugin = null;

    private final String name = "SDFEconomy";
    private SDFEconomyAPI api = null;
    
    public Economy_SDFEconomy(Plugin _plugin) {
        plugin = _plugin;

        // Register a listener to wait for plugin being loaded
        plugin.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Try and Load API in case plugin was loaded before Vault
        load_api();
    }

    public void load_api() {
        SDFEconomy pluginSDF = (SDFEconomy) plugin.getServer().getPluginManager().getPlugin("SDFEconomy");
        if(!isEnabled() && pluginSDF != null) {
            api = pluginSDF.getAPI(); 
            log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
        }
    }

    public void unload_api() {
        SDFEconomy pluginSDF = (SDFEconomy) plugin.getServer().getPluginManager().getPlugin("SDFEconomy");
        if(isEnabled() && pluginSDF != null) {
            api = null; 
            log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), name));
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_SDFEconomy economy = null;

        public EconomyServerListener(Economy_SDFEconomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (event.getPlugin().getDescription().getName().equals("SDFEconomy")) {
                economy.load_api();
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin().getDescription().getName().equals("SDFEconomy")) {
                economy.unload_api();
            }
        }
    }

 
    @Override
    public boolean isEnabled() {
        return api != null;
    }

    @Override
    public String getName() {
        return "SDFEconomy"; 
    }

    @Override
    public boolean hasBankSupport() {
        return api.hasBankSupport();
    }

    @Override
    public int fractionalDigits() {
        return api.fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return api.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return api.currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return api.currencyNameSingular();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return api.hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return api.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return api.has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return api.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return api.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return api.createBank(name, player);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return api.deleteBank(name);
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return api.bankBalance(name);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return api.bankHas(name, amount);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return api.bankWithdraw(name, amount);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return api.bankDeposit(name, amount);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return api.isBankOwner(name, playerName);
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return api.isBankMember(name, playerName);
    }

    @Override
    public List<String> getBanks() {
        return api.getBankNames();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return api.createPlayerAccount(playerName);
    }
    
}
