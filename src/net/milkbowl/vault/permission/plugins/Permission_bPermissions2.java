package net.milkbowl.vault.permission.plugins;

import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;

public class Permission_bPermissions2 extends Permission {

    private String name = "bPermissions2";
    private boolean hooked = false;

    public Permission_bPermissions2(Vault plugin) {
        this.plugin = plugin;
        
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
        
        // Load Plugin in case it was loaded before
        if (!hooked) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
            if (p != null) {
                hooked = true;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (!hooked) {
                Plugin p = event.getPlugin();
                if(p.getDescription().getName().equals("bPermissions") && p.isEnabled()) {
                    hooked = true;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (hooked) {
                if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
                    hooked = false;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
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
        return hooked;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return ApiLayer.hasPermission(world, CalculableType.USER, player, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        ApiLayer.addPermission(world, CalculableType.USER, player, de.bananaco.bpermissions.api.util.Permission.loadFromString(permission));
    	return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        ApiLayer.removePermission(world, CalculableType.USER, player, permission);
        return true;
    }

    // use superclass implementation of playerAddTransient() and playerRemoveTransient()

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return ApiLayer.hasPermission(world, CalculableType.GROUP, group, permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
    	ApiLayer.addPermission(world, CalculableType.GROUP, group, de.bananaco.bpermissions.api.util.Permission.loadFromString(permission));
    	return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        ApiLayer.removePermission(world, CalculableType.GROUP, group, permission);
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return ApiLayer.hasGroup(world, CalculableType.USER, player, group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        ApiLayer.addGroup(world, CalculableType.USER, player, group);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        ApiLayer.removeGroup(world, CalculableType.USER, player, group);
        return true;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return ApiLayer.getGroups(world, CalculableType.USER, name);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        String[] groups = getPlayerGroups(world, player);
        return groups != null ? groups[0] : null;
    }

    @Override
    public String[] getGroups() {
        String[] groups = null;
        Set<String> gSet = new HashSet<String>();
        for(World world : WorldManager.getInstance().getAllWorlds()) {
        	Set<Calculable> gr = world.getAll(CalculableType.GROUP);
        	for(Calculable c : gr) {
        		gSet.add(c.getNameLowerCase());
        	}
        }
        // Convert to String
        groups = gSet.toArray(new String[gSet.size()]);
        return groups;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }
}
