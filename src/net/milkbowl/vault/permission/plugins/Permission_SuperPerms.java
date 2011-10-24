package net.milkbowl.vault.permission.plugins;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class Permission_SuperPerms extends Permission {

	private String name = "SuperPerms";
	private Vault plugin;
	
	public Permission_SuperPerms(Vault plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean playerHas(String world, String player, String permission) {
		Player p = plugin.getServer().getPlayer(player);
		return p != null ? p.hasPermission(permission) : false;
	}

	@Override
	public boolean playerAdd(String world, String player, String permission) {
		return false;
	}

	@Override
	public boolean playerAddTransient(String world, String player, String permission) {
		if (world != null) {
			throw new UnsupportedOperationException(getName() + " does not support World based transient permissions!");
		}
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}
		
		for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
			if (paInfo.getAttachment().getPlugin().equals(plugin)) {
				paInfo.getAttachment().setPermission(permission, true);
				return true;
			}
		}
		
		PermissionAttachment attach = p.addAttachment(plugin);
		attach.setPermission(permission, true);
		
		return true;
	}

	@Override
	public boolean playerRemove(String world, String player, String permission) {
		return false;
	}

	@Override
	public boolean playerRemoveTransient(String world, String player, String permission) {
		if (world != null) {
			throw new UnsupportedOperationException(getName() + " does not support World based transient permissions!");
		}
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}
		for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
			if (paInfo.getAttachment().getPlugin().equals(plugin)) {
				return paInfo.getAttachment().getPermissions().remove(permission);
			}
		}
		return false;
	}

	@Override
	public boolean groupHas(String world, String group, String permission) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public boolean groupAdd(String world, String group, String permission) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public boolean groupRemove(String world, String group, String permission) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public boolean playerInGroup(String world, String player, String group) {
		return playerHas(world, player, "groups." + group);
	}

	@Override
	public boolean playerAddGroup(String world, String player, String group) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public boolean playerRemoveGroup(String world, String player, String group) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value) {	
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {	
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {	
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node,double value) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
		
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public String[] getPlayerGroups(String world, String player) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public String getPrimaryGroup(String world, String player) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		throw new UnsupportedOperationException(getName() + " no data permissions.");
	}

	@Override
	public String[] getGroups() {
		throw new UnsupportedOperationException(getName() + " does not support group listing!");
	}
}
