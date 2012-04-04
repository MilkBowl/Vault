# Vault - Abstraction Library for Bukkit Plugin

## Installing
Installing Vault is as simple as copying the provided "Vault.jar" to your
"<bukkit-install-dir>/plugins" directory and the rest is automatic!  If you
wish to perform configuration changes, this can be done via a configuration
file but should not be necessary in most cases.  See the "Advanced
Configuration" section for more information.


## Why Vault?
I have no preference which library suits your plugin and development efforts
best.  Really, I thought a central suite (rather...Vault) of solutions was the
the proper avenue than focusing on a single category of plugin.  That's where
the idea for Vault came into play.

So, what features do I _think_ you'll like the most?

 * No need to include my source code in your plugin
   All of Vault is run in its own plugin, so all you need to do is obtain an
   instance of it!  This simplifies issues with multiple plugins using the same
   namespaces.  Just simply add Vault.jar to your download zip file!
 * Broad range of supported plugins
   I wanted an abstraction layer not only for Economic plugins but also
   Permission plugins as well.  The future will likely add more, but what
   types, I have yet to decide, let me know!
 * Choice!
   That's half the fun of Bukkit, we get to choose what to use!  More choice
   has never hurt developers so here's to choice!


## Permissions
 * vault.admin
   - Determines if a player should recieve the update notices

## License
Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>

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

## Building
Vault comes with all libraries needed to build from the current branch and
also comes with an Apache Ant build file (build.xml).


## Dependencies
Because Vault provides a bridge to other plugins, their binaries will be
required to build from.  To ease this, they have been included in the lib
folder and will be updated from time to time.  For plugin developers, it
is not necessary to use these libraries when implementing Vault.  You will
only need to compile against Vault.


## Supported Plugins
Vault provides abstraction for the following categories and plugins.  If you
have your own plugin that you believe should be supported, fork Vault or create
a patch with the necessary changes.  Additionally you can create an issue on
Github and we'll get to it at our convenience.

 * Economy
   - BOSEconomy 6 (http://forums.bukkit.org/threads/19025/)
   - BOSEconomy 7
   - iConomy 4 (http://forums.bukkit.org/threads/40/)
   - iConomy 5 (http://forums.bukkit.org/threads/40/)
   - iConomy 6 (http://forums.bukkit.org/threads/40/)
   - 3co (http://forums.bukkit.org/threads/22461/)
   - CurrencyCore (http://dev.bukkit.org/server-mods/currency/)
   - CraftConomy (http://dev.bukkit.org/server-mods/craftconomy/)
   - MineConomy (http://dev.bukkit.org/server-mods/mineconomy/)
   - EconXP (http://dev.bukkit.org/server-mods/econxp/)
   - eWallet (http://dev.bukkit.org/server-mods/ewallet/)
   - MuliCurrency
   - Essentials Economy (http://forums.bukkit.org/threads/15312/)

 * Permissions
   - Permissions Ex (http://forums.bukkit.org/threads/18140/)
   - Permissions 3 (http://forums.bukkit.org/threads/18430/)
   - bPermissions
   - bPermissions 2 (http://dev.bukkit.org/server-mods/bpermissions/)
   - PermissionsBukkit
   - zPermissions
   - SuperPerms
   - Group Manager (Essentials) (http://forums.bukkit.org/threads/15312/)


## Implementing Vault
Implementing Vault is quite simple through obtaining an instance through the
Bukkit PluginManager class by using the string "Vault".  An example plugin with
limited functionality is located within the VaultExamplePlugin repository
(https://github.com/MilkBowl/VaultExamplePlugin).

Example:

```java
package com.example.plugin;

import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

    @Override
    public void onDisable() {
        log.info(Level.INFO, String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            log.info(Level.SEVERE, String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermission();
        setupChat();
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            log.info("Only players are supported for this Example Plugin, but you should not do this!!!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(command.getLabel().equals("test-economy")) {
            // Lets give the player 1.05 currency (note that SOME economic plugins require rounding!
            sender.sendMessage(String.format("You have %s", vault.getEconomy().format(vault.getEconomy().getBalance(player.getName()).amount)));
            EconomyResponse r = econ.depositPlayer(player.getName(), 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // Lets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(perms.hasPermission(player, "example.plugin.awesome")) {
                sender.sendMessage("You are awesome!");
            } else {
                sender.sendMessage("You suck!");
            }
            return true;
        } else {
            return false;
        }
    }
}
```