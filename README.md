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
None!  Vault has no permission nodes itself.


## Advanced Configuration
It isn't implemented yet, but the goal is to allow disabling of specific
plugins by their namespace in a <bukkit-dir>/plugins/Vault/vault.properties
configuration file.  It won't be necessary except for really oddball
situations.


## License
Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.


## Building
Vault comes with all libraries needed to build from the current branch and
also comes with an Apache Ant build file (build.xml).


## Dependencies
Because Vault provides a bridge to other plugins, their binaries will be
required to build from.  To ease this, they have been included in the lib
folder and will be updated from time to time.


## Supported Plugins
Vault provides abstraction for the following categories and plugins.  If you
have your own plugin that you believe should be supported, fork Vault or create
a patch with the necessary changes.  Additionally you can create an issue on
Github and we'll get to it at our convenience.

 * Economy
   - BOSEconomy (http://forums.bukkit.org/threads/19025/)
   - iConomy 4 & 5 (http://forums.bukkit.org/threads/40/)
   - 3co (http://forums.bukkit.org/threads/22461/)

 * Permissions
   - Permissions 2 & 3 (http://forums.bukkit.org/threads/18430/)
   - Permissions Ex (http://forums.bukkit.org/threads/18140/)


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
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    private Vault vault = null;

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
        if(x != null & x instanceof Vault) {
            vault = (Vault) x;
            log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
        } else {
            /**
             * Throw error & disable because we have Vault set as a dependency, you could give a download link
             * or even download it for the user.  This is all up to you as a developer to decide the best option
             * for your users!  For our example, we assume that our audience (developers) can find the Vault
             * plugin and properly install it.  It's usually a bad idea however.
             */
            log.warning(String.format("[%s] Vault was _NOT_ found! Disabling plugin.", getDescription().getName()));
            getPluginLoader().disablePlugin(this);
        }
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
            EconomyResponse r = vault.getEconomy().depositPlayer(player.getName(), 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", vault.getEconomy().format(r.amount), vault.getEconomy().format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // Lets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(vault.getPermission().hasPermission(player, "example.plugin.awesome", false)) {
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
