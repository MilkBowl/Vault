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
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;

public class Chat_rscPermissions extends Chat
{
	private final Plugin vault;
	private final ru.simsonic.rscPermissions.MainPluginClass rscp;
	private final ru.simsonic.rscPermissions.rscpAPI API;
	public Chat_rscPermissions(Plugin plugin, Permission perm)
	{
		super(perm);
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
	public String getPlayerPrefix(String string, String string1)
	{
		return API.getPlayerPrefix(string, string1);
	}
	@Override
	public String getPlayerSuffix(String string, String string1)
	{
		return API.getPlayerSuffix(string, string1);
	}
	@Override
	public String getGroupPrefix(String string, String string1)
	{
		return API.getGroupPrefix(string, string1);
	}
	@Override
	public String getGroupSuffix(String string, String string1)
	{
		return API.getGroupSuffix(string, string1);
	}
	@Override
	public void setPlayerPrefix(String string, String string1, String string2)
	{
		API.setPlayerPrefix(string, string1, string2);
	}
	@Override
	public void setPlayerSuffix(String string, String string1, String string2)
	{
		API.setPlayerSuffix(string, string1, string2);
	}
	@Override
	public void setGroupPrefix(String string, String string1, String string2)
	{
		API.setGroupPrefix(string, string1, string2);
	}
	@Override
	public void setGroupSuffix(String string, String string1, String string2)
	{
		API.setGroupSuffix(string, string1, string2);
	}
	@Override
	public int getPlayerInfoInteger(String string, String string1, String string2, int i)
	{
		return 0;
	}
	@Override
	public void setPlayerInfoInteger(String string, String string1, String string2, int i)
	{
	}
	@Override
	public int getGroupInfoInteger(String string, String string1, String string2, int i)
	{
		return 0;
	}
	@Override
	public void setGroupInfoInteger(String string, String string1, String string2, int i)
	{
	}
	@Override
	public double getPlayerInfoDouble(String string, String string1, String string2, double d)
	{
		return 0.0;
	}
	@Override
	public void setPlayerInfoDouble(String string, String string1, String string2, double d)
	{
	}
	@Override
	public double getGroupInfoDouble(String string, String string1, String string2, double d)
	{
		return 0.0;
	}
	@Override
	public void setGroupInfoDouble(String string, String string1, String string2, double d)
	{
	}
	@Override
	public boolean getPlayerInfoBoolean(String string, String string1, String string2, boolean bln)
	{
		return false;
	}
	@Override
	public void setPlayerInfoBoolean(String string, String string1, String string2, boolean bln)
	{
	}
	@Override
	public boolean getGroupInfoBoolean(String string, String string1, String string2, boolean bln)
	{
		return false;
	}
	@Override
	public void setGroupInfoBoolean(String string, String string1, String string2, boolean bln)
	{
	}
	@Override
	public String getPlayerInfoString(String string, String string1, String string2, String string3)
	{
		return "...";
	}
	@Override
	public void setPlayerInfoString(String string, String string1, String string2, String string3)
	{
	}
	@Override
	public String getGroupInfoString(String string, String string1, String string2, String string3)
	{
		return "...";
	}
	@Override
	public void setGroupInfoString(String string, String string1, String string2, String string3)
	{
	}
}