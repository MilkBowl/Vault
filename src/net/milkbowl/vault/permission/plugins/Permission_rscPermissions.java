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

import ru.simsonic.rscPermissions.MainPluginClass;

public class Permission_rscPermissions extends Permission {

    private final Plugin vault;
    private ru.simsonic.rscPermissions.MainPluginClass rscp = null;
    private ru.simsonic.rscPermissions.rscpAPI rscpAPI = null;

    public Permission_rscPermissions(Plugin plugin) {
        super();
        this.vault = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), vault);
        if (rscp == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("rscPermissions");
            if (perms != null && perms.isEnabled()) {
                this.rscp = (MainPluginClass) perms;
                rscpAPI = rscp.API;
                plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "rscPermissions"));
            }
        }
    }

    private class PermissionServerListener implements Listener {
        private final Permission_rscPermissions bridge;

        public PermissionServerListener(Permission_rscPermissions bridge) {
            this.bridge = bridge;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if(bridge.rscp == null) {
                Plugin plugin = event.getPlugin();
                if (plugin.getDescription().getName().equals("rscPermissions")) {
                    bridge.rscp = (MainPluginClass) plugin;
                    bridge.rscpAPI = bridge.rscp.API;
                    log.info(String.format("[%s][Permission] %s hooked.", vault.getDescription().getName(), "rscPermissions"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if(bridge.rscpAPI != null){
                if(event.getPlugin().getDescription().getName().equals(bridge.rscpAPI.getName())) {
                    bridge.rscpAPI = null;
                    bridge.rscp = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", vault.getDescription().getName(), "rscPermissions"));
                }
            }
        }
    }

    @Override
    public String getName() {
        return "rscPermissions";
    }

    @Override
    public boolean isEnabled() {
        return rscpAPI != null && rscpAPI.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return rscpAPI.hasSuperPermsCompat();
    }

    @Override
    public boolean hasGroupSupport() {
        return rscpAPI.hasGroupSupport();
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return rscpAPI.playerHas(world, player, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return rscpAPI.playerAdd(world, player, permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return rscpAPI.playerRemove(world, player, permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return rscpAPI.groupHas(world, group, permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return rscpAPI.groupAdd(world, group, permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return rscpAPI.groupRemove(world, group, permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return rscpAPI.playerInGroup(world, player, group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return rscpAPI.playerAddGroup(world, player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return rscpAPI.playerRemoveGroup(world, player, group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return rscpAPI.getPlayerGroups(world, player);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return rscpAPI.getPrimaryGroup(world, player);
    }

    @Override
    public String[] getGroups() {
        return rscpAPI.getGroups();
    }
}
