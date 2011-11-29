package net.milkbowl.vault.permission.plugins;

import org.bukkit.entity.Player;

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

	// use superclass implementation of playerAddTransient() and playerRemoveTransient()

	@Override
	public boolean playerRemove(String world, String player, String permission) {
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
		return new String[0];
	}

	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
}
