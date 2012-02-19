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

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;

public class Chat_Towny extends Chat {

    private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = "Towny";
    private Towny towny;
    private Vault plugin;


    public Chat_Towny(Vault plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
        
        // Load Plugin in case it was loaded before
        if (towny == null) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("Towny");
            if (p != null) {
                towny = (Towny) p;
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "Towny"));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Chat_Towny chat = null;

        public PermissionServerListener(Chat_Towny chat) {
            this.chat = chat;
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (this.chat.towny == null) {
                Plugin towny = plugin.getServer().getPluginManager().getPlugin("Towny");
                if (chat != null) {
                    this.chat.towny = (Towny) towny;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "Towny"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (this.chat.towny != null) {
                if (event.getPlugin().getDescription().getName().equals("Towny")) {
                    this.chat.towny = null;
                    log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "Towny"));
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
        return towny != null;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        try {
            Resident r = towny.getTownyUniverse().getResident(player);
            return r.getTitle();
        } catch (NotRegisteredException e) {
        }
        return "";
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        throw new UnsupportedOperationException("Towny does not support altering prefixes");
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        try {
            Resident r = towny.getTownyUniverse().getResident(player);
            return r.getSurname();
        } catch (NotRegisteredException e) {
        }
        return "";
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        throw new UnsupportedOperationException("Towny does not support altering suffixes");
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        try {
            return towny.getTownyUniverse().getTown(group).getTag();
        } catch (NotRegisteredException e) {
        }
        return "";
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        throw new UnsupportedOperationException("Towny does not support altering prefixes");
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        throw new UnsupportedOperationException("Towny does not support group/town suffixes");
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        throw new UnsupportedOperationException("Towny does not support altering suffixes");
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

    @Override
    public void setGroupInfoString(String world, String group, String node,  String value) {
        throw new UnsupportedOperationException("Towny does not support info nodes");
    }

}
