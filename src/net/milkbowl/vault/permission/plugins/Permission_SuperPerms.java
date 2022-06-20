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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Permission_SuperPerms extends Permission {
	
	public Permission_SuperPerms(final Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getName() {
		return "SuperPerms";
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		final Player p = this.plugin.getServer().getPlayer(player);
		return p != null && p.hasPermission(permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		return false;
	}
	
	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		return false;
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		return this.playerHas(world, player, "groups." + group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		throw new UnsupportedOperationException(this.getName() + " no group permissions.");
	}
	
	@Override
	public String[] getGroups() {
		return new String[0];
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	public boolean hasGroupSupport() {
		return false;
	}
}
