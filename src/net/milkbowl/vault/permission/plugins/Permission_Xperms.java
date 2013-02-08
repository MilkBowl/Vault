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
package net.milkbowl.vault.permission.plugins;

import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import com.github.sebc722.Xperms.Xmain;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_Xperms extends Permission {

    private final String name = "Xperms";
    private Xmain permission = null;

    public Permission_Xperms(Plugin plugin) {
    	this.plugin = plugin;
    	Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin)
    	
    	if(permission == null){
    		Plugin perms = plugin.getServer().getPluginManager().getPlugin("Xperms");
    		if(perms != null){
    			if(perms.isEnabled()){
    				try{
    					if(Double.valueOf(perms.getDescription().getVersion()) < 1.1){
    						log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", plugin.getDescription().getName(), name));
    					}
    				} catch(NumberFormatException e){
    					// version is first release, numbered 1.0.0
    					log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", plugin.getDescription().getName(), name);
    				}
    			}
    			permission = (Xmain) perms;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
    		}
    	}
    }

    public class PermissionServerListener implements Listener {
    	Xmain permission = null;
    	
    	public PermissionServerListener(Permission_Xperms permission){
    		this.permission = permission;
    	}
    	
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
        	this.plugin = plugin;
        	Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin)
        	
        	if(permission == null){
        		Plugin perms = plugin.getServer().getPluginManager().getPlugin("Xperms");
        		if(perms != null){
        			if(perms.isEnabled()){
        				try{
        					if(Double.valueOf(perms.getDescription().getVersion()) < 1.1){
        						log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", plugin.getDescription().getName(), name));
        					}
        				} catch(NumberFormatException e){
        					// version is first release, numbered 1.0.0
        					log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", plugin.getDescription().getName(), name);
        				}
        			}
        			permission.permission = (Xmain) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
        		}
        	}
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
        	if(permission.permission != null){
        		if(event.getPlugin().getName().equals("Xperms")){
        			permission.permission = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
        		}
        	}
        }
    }

    @Override
    public String getName() {
    	return name;
    }

    @Override
    public boolean isEnabled() {
    	return permission.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
    	return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
    	return permission.getXplayer().hasPermission(player, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
    	return permission.getXplayer().addPermission(player, permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
    	return permission.getXplayer().removePermission(player, permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
    	return permission.getXgroup().hasPermission(group, permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
    	return permission.getXgroup().addPermission(group, permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
    	return permission.getXgroup().removePermission(group, permission)
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
    	String userGroup = permission.getXusers().getUserGroup(player);
    	if(userGroup == group){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
    	return permission.getXplayer().setGroup(player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
    	return permission.getXplayer().setGroup(player, "def");
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
    	ArrayList<String> playerGroup = new ArrayList<String>();
    	playerGroup.add(permission.getXusers().getUserGroup(player));
    	String[] playerGroupArray = playerGroup.toArray(new String[0]);
    	
    	return playerGroupArray;
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
    	return permission.getXusers().getUserGroup(player);
    }

    @Override
    public String[] getGroups() {
    	return permission.getXperms().getGroups();
    }
}