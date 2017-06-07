/*
 * This file is part of Vault.
 *
 * Copyright (c) 2017 Lukas Nehrke
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

import com.gmail.bleedobsidian.miconomy.Main;
import com.gmail.bleedobsidian.miconomy.MiConomy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Economy_MiConomy extends AbstractEconomy {
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private final String name = "MiConomy";
    
    private Plugin plugin;
    private MiConomy economy;
    private Main miConomy;
    
    public Economy_MiConomy(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        
        // Load Plugin in case it was loaded before
        if (miConomy == null) {
            Plugin miConomyPlugin = plugin.getServer().getPluginManager().getPlugin("MiConomy");
            
            if (miConomy != null) {
                miConomy = (Main) miConomyPlugin;
                economy = miConomy.getInstance();
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }
    
    @Override
    public boolean isEnabled() {
        if(miConomy == null) {
            return false;
        } else {
            return miConomy.isEnabled();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return economy.getFormattedValue(amount);
    }

    @Override
    public String currencyNamePlural() {
        return miConomy.getPluginConfig().MoneyNamePlural;
    }

    @Override
    public String currencyNameSingular() {
        return miConomy.getPluginConfig().MoneyName;
    }

    @Override
    public boolean hasAccount(String playerName) {
        List<World> worlds = plugin.getServer().getWorlds();
        
        return hasAccount(playerName, worlds.get(0).getName());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        World world = plugin.getServer().getWorld(worldName);
        
        return economy.isAccountCreated(player, world);
    }

    @Override
    public double getBalance(String playerName) {
        List<World> worlds = plugin.getServer().getWorlds();
        
        return getBalance(playerName, worlds.get(0).getName());
    }

    @Override
    public double getBalance(String playerName, String worldName) {   
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        World world = plugin.getServer().getWorld(worldName);
        
        return economy.getAccountBalance(player, world);
    }

    @Override
    public boolean has(String playerName, double amount) {
        List<World> worlds = plugin.getServer().getWorlds();
        
        return has(playerName, worlds.get(0).getName(), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        World world = plugin.getServer().getWorld(worldName);
        
        double playerBalance = economy.getAccountBalance(player, world);
        
        if(playerBalance >= amount) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        List<World> worlds = plugin.getServer().getWorlds();
        
        return withdrawPlayer(playerName, worlds.get(0).getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        World world = plugin.getServer().getWorld(worldName);
        
        double balance = economy.getAccountBalance(player, world);
        
        if(getBalance(playerName, worldName) < amount) {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        } else {
            if(economy.removeAccountBalance(player, amount, world)) {
                balance = economy.getAccountBalance(player, world);
                
                return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Failed to remove funds from account");
            }
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        List<World> worlds = plugin.getServer().getWorlds();
        
        return depositPlayer(playerName, worlds.get(0).getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        World world = plugin.getServer().getWorld(worldName);
        
        double balance = economy.getAccountBalance(player, world);
        
        if(economy.addAccountBalance(player, amount, world)) {
            balance = economy.getAccountBalance(player, world);

            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Failed to add funds to account");
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        OfflinePlayer owner = plugin.getServer().getOfflinePlayer(player);
        
        ArrayList<OfflinePlayer> owners = new ArrayList<OfflinePlayer>();
        owners.add(owner);
        
        if(!economy.isBankCreated(name)) {
            economy.createBank(name, owners, new ArrayList<String>(), false);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "A bank with this name already exists");
        }
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        if(economy.isBankCreated(name)) {
            economy.deleteBank(name);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if(economy.isBankCreated(name)) {
            double balance = economy.getBankBalance(name);
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        if(economy.isBankCreated(name)) {
            double balance = economy.getBankBalance(name);
            
            if(balance >= amount) {
                return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
            }
        } else {
             return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        if(economy.isBankCreated(name)) {
            economy.removeBankBalance(name, amount);
            
            double balance = economy.getBankBalance(name);
            
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if(economy.isBankCreated(name)) {
            economy.addBankBalance(name, amount);
            
            double balance = economy.getBankBalance(name);
            
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        OfflinePlayer owner = plugin.getServer().getOfflinePlayer(playerName);
        
        if(economy.isBankCreated(name)) {
            if(economy.isPlayerBankOwner(name, owner)) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "The player is not a bank owner");
            }
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        OfflinePlayer owner = plugin.getServer().getOfflinePlayer(playerName);
        
        if(economy.isBankCreated(name)) {
            if(economy.isPlayerBankMember(name, owner)) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "The player is not a bank member");
            }
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
        }
    }

    @Override
    public List<String> getBanks() {
        return economy.getBanks();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        List<World> worlds = plugin.getServer().getWorlds();
        
        return createPlayerAccount(playerName, worlds.get(0).getName());
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
        World world = plugin.getServer().getWorld(worldName);
        
        if(!economy.isAccountCreated(player, world)) {
            economy.createAccount(player, 0, world);
            
            return true;
        } else {
            return false;
        }
    }
    
    public class EconomyServerListener implements Listener {
        Economy_MiConomy economy = null;

        public EconomyServerListener(Economy_MiConomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin miConomyPlugin = event.getPlugin();

                if (miConomyPlugin.getDescription().getName().equals("MiConomy")) {
                    economy.miConomy = (Main) miConomyPlugin;
                    
                    economy.economy = miConomy.getInstance();
                    
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("MiConomy")) {
                    economy.miConomy = null;
                    economy.economy = null;
                    
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }
}
