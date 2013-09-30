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

import net.milkbowl.vault.permission.Permission;

import com.github.sebc722.xperms.core.Main;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_Xperms extends Permission {

    private final String name = "Xperms";
    private Main perms = null;

    public Permission_Xperms(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        if(perms == null){
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Xperms");
            if(this.perms != null){
                if(perms.isEnabled()){
                    try{
                        if(Double.valueOf(perms.getDescription().getVersion()) < 1.1){
                            log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", plugin.getDescription().getName(), name));
                        }
                    } catch(NumberFormatException e){
                        // version is first release, numbered 1.0.0
                        log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", plugin.getDescription().getName(), name));
                    }
                }
                this.perms = (Main) perms;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Permission_Xperms permission = null;

        public PermissionServerListener(Permission_Xperms permission){
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.perms == null) {
                Plugin perms = event.getPlugin();
                if(perms.getDescription().getName().equals("Xperms")){
                    try{
                        if(Double.valueOf(perms.getDescription().getVersion()) < 1.1){
                            log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", plugin.getDescription().getName(), name));
                        }
                    } catch(NumberFormatException e){
                        // version is first release, numbered 1.0.0
                        log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", plugin.getDescription().getName(), name));
                    }
                    permission.perms = (Main) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if(permission.perms != null){
                if(event.getPlugin().getName().equals("Xperms")){
                    permission.perms = null;
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
        return perms.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return perms.getXplayer().hasPerm(world, player, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return perms.getXplayer().addNode(world, player, permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return perms.getXplayer().removeNode(world, player, permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return perms.getXgroup().hasPerm(group, permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        perms.getXgroup().addNode(group, permission);
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return perms.getXgroup().removeNode(group, permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        String groupForWorld = perms.getXplayer().getGroupForWorld(player, world);
        if(groupForWorld.equals(group)){
            return true;
        }
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return perms.getXplayer().setPlayerGroup(world, player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return perms.getXplayer().setPlayerDefault(world, player);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return perms.getXplayer().getPlayerGroups(player);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return perms.getXplayer().getGroupForWorld(player, world);
    }

    @Override
    public String[] getGroups() {
        return perms.getXgroup().getGroups();
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}