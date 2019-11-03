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

import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Permission_PermissionsEx extends Permission {

    private final String name = "PermissionsEx";
    private PermissionsEx permission = null;

    public Permission_PermissionsEx(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (perms != null) {
                if (perms.isEnabled()) {
                    try {
                        if (Double.valueOf(perms.getDescription().getVersion()) < 1.16) {
                            log.info(String.format(
                                "[Permission] %s below 1.16 is not compatible with Vault! Falling back to SuperPerms only mode. PLEASE UPDATE!", name));
                        }
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                    permission = (PermissionsEx) perms;
                    log.info(String.format("[Permission] %s hooked.", name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (permission == null) {
            return false;
        } else {
            return permission.isEnabled();
        }
    }

    public class PermissionServerListener implements Listener {
        Permission_PermissionsEx permission = null;

        public PermissionServerListener(Permission_PermissionsEx permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = event.getPlugin();
                if (perms.getDescription().getName().equals("PermissionsEx")) {
                    try {
                        if (Double.valueOf(perms.getDescription().getVersion()) < 1.16) {
                            log.info(String.format(
                                "[Permission] %s below 1.16 is not compatible with Vault! Falling back to SuperPerms only mode. PLEASE UPDATE!", name));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                    permission.permission = (PermissionsEx) perms;
                    log.info(String.format("[Permission] %s hooked.", permission.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.permission != null) {
                if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
                    permission.permission = null;
                    log.info(String.format("[Permission] %s un-hooked.", permission.name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean playerInGroup(String worldName, OfflinePlayer op, String groupName) {
    	PermissionUser user = getUser(op);
    	if (user == null) {
    		return false;
    	}
    	return user.inGroup(groupName, worldName);
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        return PermissionsEx.getPermissionManager().getUser(playerName).inGroup(groupName, worldName);
    }

    @Override
    public boolean playerAddGroup(String worldName, OfflinePlayer op, String groupName) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        PermissionUser user = getUser(op);
        if (group == null || user == null) {
            return false;
        } else {
            user.addGroup(groupName, worldName);
            return true;
        }
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (group == null || user == null) {
            return false;
        } else {
            user.addGroup(groupName, worldName);
            return true;
        }
    }

    @Override
    public boolean playerRemoveGroup(String worldName, OfflinePlayer op, String groupName) {
    	PermissionUser user = getUser(op);
    	user.removeGroup(groupName, worldName);
    	return true;
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        PermissionsEx.getPermissionManager().getUser(playerName).removeGroup(groupName, worldName);
        return true;
    }

    @Override
    public boolean playerAdd(String worldName, OfflinePlayer op, String permission) {
        PermissionUser user = getUser(op);
        if (user == null) {
            return false;
        } else {
            user.addPermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        PermissionUser user = getUser(playerName);
        if (user == null) {
            return false;
        } else {
            user.addPermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean playerRemove(String worldName, OfflinePlayer op, String permission) {
        PermissionUser user = getUser(op);
        if (user == null) {
            return false;
        } else {
            user.removePermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        PermissionUser user = getUser(playerName);
        if (user == null) {
            return false;
        } else {
            user.removePermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            group.addPermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            group.removePermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            return group.has(permission, worldName);
        }
    }

    private PermissionUser getUser(OfflinePlayer op) {
    	return PermissionsEx.getPermissionManager().getUser(op.getUniqueId());
    }
    
    private PermissionUser getUser(String playerName) {
    	return PermissionsEx.getPermissionManager().getUser(playerName);
    }

    @Override
    public String[] getPlayerGroups(String world, OfflinePlayer op) {
    	PermissionUser user = getUser(op);
    	return user == null ? null : user.getParentIdentifiers(world).toArray(new String[0]);
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
    	PermissionUser user = getUser(playerName);
    	return user == null ? null : user.getParentIdentifiers(world).toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, OfflinePlayer op) {
    	PermissionUser user = getUser(op);
    	if (user == null) {
    		return null;
    	} else if (user.getParentIdentifiers(world).size() > 0) {
    		return user.getParentIdentifiers(world).get(0);
    	} else {
    		return null;
    	}
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user == null) {
            return null;
        } else if (user.getParentIdentifiers(world).size() > 0) {
            return user.getParentIdentifiers(world).get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean playerHas(String worldName, OfflinePlayer op, String permission) {
    	PermissionUser user = getUser(op);
        if (user != null) {
            return user.has(permission, worldName);
        } else {
            return false;
        }
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        PermissionUser user = getUser(playerName);
        if (user != null) {
            return user.has(permission, worldName);
        } else {
            return false;
        }
    }


    @Override
    public boolean playerAddTransient(String worldName, Player player, String permission) {
    	PermissionUser pPlayer = getUser(player);
        if (pPlayer != null) {
            pPlayer.addTimedPermission(permission, worldName, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean playerAddTransient(Player player, String permission) {
        return playerAddTransient(null, player, permission);
    }


    @Override
    public boolean playerRemoveTransient(Player player, String permission) {
        return playerRemoveTransient(null, player, permission);
    }

    @Override
    public boolean playerRemoveTransient(String worldName, Player player, String permission) {
        PermissionUser pPlayer = getUser(player);
        if (pPlayer != null) {
            pPlayer.removeTimedPermission(permission, worldName);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getGroups() {
        List<PermissionGroup> groups = PermissionsEx.getPermissionManager().getGroupList();
        if (groups == null || groups.isEmpty()) {
            return null;
        }
        String[] groupNames = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupNames[i] = groups.get(i).getName();
        }
        return groupNames;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
