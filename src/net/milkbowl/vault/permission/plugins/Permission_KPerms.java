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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

import com.lightniinja.kperms.KPlayer;
import com.lightniinja.kperms.KGroup;
import com.lightniinja.kperms.Utilities;
import com.lightniinja.kperms.KPermsPlugin;

public class Permission_KPerms extends Permission {

    private final Plugin vault;
    private KPermsPlugin kperms = null;

    public Permission_KPerms(Plugin plugin) {
        super();
        this.vault = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), vault);
        if (kperms == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("KPerms");
            if (perms != null && perms.isEnabled()) {
                this.kperms = (KPermsPlugin) perms;
                plugin.getLogger().info(String.format("[Permission] %s hooked.", "KPerms"));
            }
        }
    }

    private class PermissionServerListener implements Listener {
        private final Permission_KPerms bridge;

        public PermissionServerListener(Permission_KPerms bridge) {
            this.bridge = bridge;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if(bridge.kperms == null) {
                Plugin plugin = event.getPlugin();
                if (plugin.getDescription().getName().equals("KPerms")) {
                    bridge.kperms = (KPermsPlugin) plugin;
                    log.info(String.format("[Permission] %s hooked.", "KPerms"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if(bridge.kperms != null){
                if(event.getPlugin().getDescription().getName().equals(bridge.kperms.getName())) {
                    bridge.kperms = null;
                    log.info(String.format("[Permission] %s un-hooked.", "KPerms"));
                }
            }
        }
    }

    @Override
    public String getName() {
        return "KPerms";
    }

    @Override
    public boolean isEnabled() {
        return kperms.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return new KPlayer(player, kperms).hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return new KPlayer(player, kperms).addPermission(permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return new KPlayer(player, kperms).removePermission(permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return new KGroup(group, kperms).hasPermission(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return new KGroup(group, kperms).addPermission(permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return new KGroup(group, kperms).removePermission(permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return new KPlayer(player, kperms).isMemberOfGroup(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return new KPlayer(player, kperms).addGroup(group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return new KPlayer(player, kperms).removeGroup(group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        List<String> groups = new KPlayer(player, kperms).getGroups();
        String[] gr = new String[groups.size()];
        gr = groups.toArray(gr);
        return gr;
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return new KPlayer(player, kperms).getPrimaryGroup();
    }

    @Override
    public String[] getGroups() {
        return new Utilities(kperms).getGroups();
    }
}
