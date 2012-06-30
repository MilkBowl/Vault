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
package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Chat_PermissionsEx extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = "PermissionsEx_Chat";
    
    private Plugin plugin = null;
    private PermissionsEx chat = null;

    public Chat_PermissionsEx(Plugin plugin, Permission perms) {
    	super(perms);
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (chat == null) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (p != null) {
                if (p.isEnabled()) {
                    chat = (PermissionsEx) p;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }
    
    public class PermissionServerListener implements Listener {
        Chat_PermissionsEx chat = null;

        public PermissionServerListener(Chat_PermissionsEx chat) {
            this.chat = chat;
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (chat.chat == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        chat.chat = (PermissionsEx) perms;
                        log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), chat.name));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (chat.chat != null) {
                if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
                    chat.chat = null;
                    log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), chat.name));
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
		if (chat == null)
			return false;
		else
			return chat.isEnabled();
	}
	
    @Override
    public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionInteger(node, world, defaultValue);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionDouble(node, world, defaultValue);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionBoolean(node, world, defaultValue);
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOption(node, world, defaultValue);
    }
    
    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionInteger(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionDouble(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionBoolean(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOption(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, value);
        }
    }
    
    @Override
    public String getPlayerPrefix(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.getPrefix(world);
        } else {
            return null;
        }
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.getSuffix(world);
        } else {
            return null;
        }
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user != null) {
            user.setSuffix(suffix, world);
        }
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user != null) {
            user.setPrefix(prefix, world);
        }
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            return pGroup.getPrefix(world);
        } else {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            pGroup.setPrefix(prefix, world);
        }

    }

    @Override
    public String getGroupSuffix(String world, String group) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            return pGroup.getSuffix(world);
        } else {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            pGroup.setSuffix(suffix, world);
        }
    }
}
