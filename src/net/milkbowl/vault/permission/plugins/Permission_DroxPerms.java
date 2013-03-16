package net.milkbowl.vault.permission.plugins;

import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.DroxPermsAPI;

public class Permission_DroxPerms extends Permission {

    private final String name = "DroxPerms";
    private DroxPermsAPI API;

    public Permission_DroxPerms(Plugin plugin) {
        this.plugin = plugin;

        // Load Plugin in case it was loaded before
        if (API == null) {
            DroxPerms p = (DroxPerms) plugin.getServer().getPluginManager().getPlugin("DroxPerms");
            if (p != null) {
                API = p.getAPI();
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "DroxPerms"));
            }
        }

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
    }

    public class PermissionServerListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (API == null) {
                Plugin permPlugin = event.getPlugin();
                if (permPlugin.getDescription().getName().equals("DroxPerms")) {
                    API = ((DroxPerms) permPlugin).getAPI();
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if(API != null) {
                if(event.getPlugin().getDescription().getName().equals("DroxPerms")) {
                    API = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        Player p = plugin.getServer().getPlayer(player);
        return p != null ? p.hasPermission(permission) : false;
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return API.addPlayerPermission(player, world, permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return API.removePlayerPermission(player, world, permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return false; //Droxperms doesn't support direct permission checking of groups
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return API.addGroupPermission(group, world, permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return API.removeGroupPermission(group, world, permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return API.getPlayerGroup(player).equalsIgnoreCase(group) || API.getPlayerSubgroups(player).contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return API.addPlayerSubgroup(player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return API.removePlayerSubgroup(player, group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        ArrayList<String> array = API.getPlayerSubgroups(player);
        array.add(API.getPlayerGroup(player));
        return array.toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return API.getPlayerGroup(player);
    }

    @Override
    public String[] getGroups() {
        return API.getGroupNames();
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

}