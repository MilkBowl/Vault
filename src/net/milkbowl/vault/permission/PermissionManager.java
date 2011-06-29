/**
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package net.milkbowl.vault.permission;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class PermissionManager {
    
    private JavaPlugin plugin = null;
    private TreeMap<Integer,Permission> perms = new TreeMap<Integer,Permission>();
    private Permission activePermission = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * Constructs a new instance of PermissionManager provided an instance of a JavaPlugin
     * @param plugin Your plugin (should be "this")
     */
    public PermissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        
        // Try to load PermissionsEx
        if(packageExists(new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" })) {
            Permission ePerms = new Permission_PermissionsEx(plugin);
            perms.put(8, ePerms);
            log.info(String.format("[%s][Permission] PermissionsEx found: %s", plugin.getDescription().getName(), ePerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] PermissionsEx not found.", plugin.getDescription().getName()));
        }

        // Try to load Permissions (Phoenix)
        if (packageExists(new String[] { "com.nijikokun.bukkit.Permissions.Permissions" })) {
            Permission nPerms = new Permission_Permissions(plugin);
            perms.put(9, nPerms);
            log.info(String.format("[%s][Permission] Permissions (Phoenix) found: %s", plugin.getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] Permissions (Phoenix) not found.", plugin.getDescription().getName()));
        }
    }
    
    private boolean packageExists(String[] packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Permission getPermission() {
        if(activePermission == null) {
            Iterator<Permission> it = perms.values().iterator();
            while(it.hasNext()) {
                Permission p = it.next();
                if(p.isEnabled()) {
                    return p;
                }
            }
            return null;
        } else {
            return activePermission;
        }
    }
    
    /**
     * Check if player has a permission node
     * @param player Player name
     * @param permission Permission node (ie: pluginname.function)
     * @return Value
     */
    public boolean hasPermission(Player player, String permission, boolean def) {
        Permission p = getPermission();
        if(p != null) {
            return p.playerHasPermission(player, permission);
        } else {
            if(player.isOp()) {
                return true;
            } else {
                return def;
            }
        }
    }
    
    /**
     * Check if player is in a group
     * @param worldName World name
     * @param playerName Player name
     * @param groupName Group name
     * @return Value
     */
    public boolean inGroup(String worldName, String playerName, String groupName) {
        Permission p = getPermission();
        if(p != null) {
            return p.playerInGroup(worldName, playerName, groupName);
        } else {
            return false;
        }
    }
    
    /**
     * Get integer value from an info node
     * @param world World name
     * @param playerName Player name
     * @param node Node name
     * @return Value
     */
    public int getInfoInt(String world, String playerName, String node, int defaultValue) {
        Permission p = getPermission();
        if(p != null) {
            return p.getPlayerInfoInteger(world, playerName, node, defaultValue);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * Get double value from an info node
     * @param world World name
     * @param playerName Player name
     * @param node Node name
     * @return Value
     */
    public double getInfoBoolean(String world, String playerName, String node, double defaultValue) {
        Permission p = getPermission();
        if(p != null) {
            return p.getPlayerInfoDouble(world, playerName, node, defaultValue);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * Get boolean value from an info node
     * @param world World name
     * @param playerName Player name
     * @param node Node name
     * @return Value
     */
    public boolean getInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        Permission p = getPermission();
        if(p != null) {
            return p.getPlayerInfoBoolean(world, playerName, node, defaultValue);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * Get boolean value from an info node
     * @param world World name
     * @param playerName Player name
     * @param node Node name
     * @return Value
     */
    public String getInfoBoolean(String world, String playerName, String node, String defaultValue) {
        Permission p = getPermission();
        if(p != null) {
            return p.getPlayerInfoString(world, playerName, node, defaultValue);
        } else {
            return defaultValue;
        }
    }

}