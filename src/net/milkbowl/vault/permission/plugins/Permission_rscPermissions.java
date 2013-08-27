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
import org.bukkit.plugin.Plugin;

public class Permission_rscPermissions extends Permission
{
	private final Plugin vault;
	private final ru.simsonic.rscPermissions.MainPluginClass rscp;
	private final ru.simsonic.rscPermissions.rscpAPI API;
	public Permission_rscPermissions(Plugin plugin)
	{
		super();
		this.vault = plugin;
		this.rscp = (ru.simsonic.rscPermissions.MainPluginClass)vault.getServer().getPluginManager().getPlugin("rscPermissions");
		this.API = (rscp != null) ? rscp.API : null;
	}
	@Override
	public String getName()
	{
		return API.getName();
	}
	@Override
	public boolean isEnabled()
	{
		return API.isEnabled();
	}
	@Override
	public boolean hasSuperPermsCompat()
	{
		return API.hasSuperPermsCompat();
	}
	@Override
	public boolean hasGroupSupport()
	{
		return API.hasGroupSupport();
	}
	@Override
	public boolean playerHas(String string, String string1, String string2)
	{
		return API.playerHas(string, string1, string2);
	}
	@Override
	public boolean playerAdd(String string, String string1, String string2)
	{
		return API.playerAdd(string, string1, string2);
	}
	@Override
	public boolean playerRemove(String string, String string1, String string2)
	{
		return API.playerRemove(string, string1, string2);
	}
	@Override
	public boolean groupHas(String string, String string1, String string2)
	{
		return API.groupHas(string, string1, string2);
	}
	@Override
	public boolean groupAdd(String string, String string1, String string2)
	{
		return API.groupAdd(string, string1, string2);
	}
	@Override
	public boolean groupRemove(String string, String string1, String string2)
	{
		return API.groupRemove(string, string1, string2);
	}
	@Override
	public boolean playerInGroup(String string, String string1, String string2)
	{
		return API.playerInGroup(string, string1, string2);
	}
	@Override
	public boolean playerAddGroup(String string, String string1, String string2)
	{
		return API.playerAddGroup(string, string1, string2);
	}
	@Override
	public boolean playerRemoveGroup(String string, String string1, String string2)
	{
		return API.playerRemoveGroup(string, string1, string2);
	}
	@Override
	public String[] getPlayerGroups(String string, String string1)
	{
		return API.getPlayerGroups(string, string1);
	}
	@Override
	public String getPrimaryGroup(String string, String string1)
	{
		return API.getPrimaryGroup(string, string1);
	}
	@Override
	public String[] getGroups()
	{
		return API.getGroups();
	}
}