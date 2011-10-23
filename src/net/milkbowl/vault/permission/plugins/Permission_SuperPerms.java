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
	public String[] getPlayerGroups(String world, String player) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public String getPrimaryGroup(String world, String player) {
		throw new UnsupportedOperationException(getName() + " no group permissions.");
	}

	@Override
	public String[] getGroups() {
		throw new UnsupportedOperationException(getName() + " does not support group listing!");
	}
}
