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

import java.util.logging.Logger;

import org.bukkit.entity.Player;

public interface Permission {
    
    public static final Logger log = Logger.getLogger("Minecraft");

    public String getName();
    public boolean isEnabled();
    
    public boolean hasPermission(Player player, String permission);
    public boolean userAddPermission(String worldName, String playerName, String permission);
    public boolean userRemovePermission(String worldName, String playerName, String permission);
    public boolean groupAddPermission(String worldName, String groupName, String permission);
    public boolean groupRemovePermission(String worldName, String groupName, String permission);
    
    public boolean inGroup(String worldName, String playerName, String groupName);
    public boolean userAddGroup(String worldName, String playerName, String groupName);
    public boolean userRemoveGroup(String worldName, String playerName, String groupName);
    
    public int getUserInfoInteger(String world, String playerName, String node, int defaultValue);
    public void setUserInfoInteger(String world, String playerName, String node, int value);
    
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue);
    public void setGroupInfoInteger(String world, String groupName, String node, int value);
    
    public double getUserInfoDouble(String world, String playerName, String node, double defaultValue);
    public void setUserInfoDouble(String world, String playerName, String node, double value);
    
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue);
    public void setGroupInfoDouble(String world, String groupName, String node, double value);
    
    public boolean getUserInfoBoolean(String world, String playerName, String node, boolean defaultValue);
    public void setUserInfoBoolean(String world, String playerName, String node, boolean value);
    
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue);
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value);
    
    public String getUserInfoString(String world, String playerName, String node, String defaultValue);
    public void setUserInfoString(String world, String playerName, String node, String value);
    
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue);
    public void setGroupInfoString(String world, String groupName, String node, String value);
    
}
