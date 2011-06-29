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

package net.milkbowl.vault;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.EconomyManager;
import net.milkbowl.vault.permission.PermissionManager;

import org.bukkit.plugin.java.JavaPlugin;

public class Vault extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    private static EconomyManager econManager = null;
    private static PermissionManager permManager = null;

    @Override
    public void onDisable() {
        econManager = null;
        permManager = null;
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        econManager = new EconomyManager(this);
        permManager = new PermissionManager(this);
        
        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    public static EconomyManager getEconomy() {
        return econManager;
    }
    
    public static PermissionManager getPermission() {
        return permManager;
    }
}
