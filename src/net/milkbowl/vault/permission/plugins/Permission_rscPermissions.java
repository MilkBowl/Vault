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
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.simsonic.rscPermissions.MainPluginClass;

public class Permission_rscPermissions extends Permission
{
	private final Plugin vault;
	private ru.simsonic.rscPermissions.MainPluginClass rscp = null;
	private ru.simsonic.rscPermissions.rscpAPI API = null;
	public Permission_rscPermissions(Plugin plugin)
	{
		super();
		this.vault = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), vault);
		rscp = (MainPluginClass)vault.getServer().getPluginManager().getPlugin("rscPermissions");
	}
	private class PermissionServerListener implements Listener
	{
		private final Permission_rscPermissions bridge;
		public PermissionServerListener(Permission_rscPermissions bridge)
		{
			this.bridge = bridge;
		}
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event)
		{
			if(bridge.rscp == null)
			{
				Plugin plugin = event.getPlugin();
				if(!"rscPermissions".equals(plugin.getName()))
					return;
				bridge.rscp = (MainPluginClass)plugin;
			}
			if(bridge.API == null)
				bridge.API = bridge.rscp.API;
			if(bridge.API.isEnabled())
				log.info(String.format("[%s][Permission] %s hooked.",
					vault.getDescription().getName(), bridge.API.getName()));
		}
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event)
		{
			if(bridge.API != null)
				if(event.getPlugin().getDescription().getName().equals(bridge.API.getName()))
				{
					log.info(String.format("[%s][Permission] %s un-hooked.",
						vault.getDescription().getName(), bridge.API.getName()));
					bridge.API = null;
				}
		}
	}
	@Override
	public String getName()
	{
		return (API != null) ? API.getName() : "rscPermissions";
	}
	@Override
	public boolean isEnabled()
	{
		return (API != null) ? API.isEnabled() : false;
	}
	@Override
	public boolean hasSuperPermsCompat()
	{
		return (API != null) ? API.hasSuperPermsCompat() : true;
	}
	@Override
	public boolean hasGroupSupport()
	{
		return (API != null) ? API.hasGroupSupport() : true;
	}
	@Override
	public boolean playerHas(String string, String string1, String string2)
	{
		if(API != null)
			return API.playerHas(string, string1, string2);
		return false;
	}
	@Override
	public boolean playerAdd(String string, String string1, String string2)
	{
		if(API != null)
			return API.playerAdd(string, string1, string2);
		return false;
	}
	@Override
	public boolean playerRemove(String string, String string1, String string2)
	{
		if(API != null)
			return API.playerRemove(string, string1, string2);
		return false;
	}
	@Override
	public boolean groupHas(String string, String string1, String string2)
	{
		if(API != null)
			return API.groupHas(string, string1, string2);
		return false;
	}
	@Override
	public boolean groupAdd(String string, String string1, String string2)
	{
		if(API != null)
			return API.groupAdd(string, string1, string2);
		return false;
	}
	@Override
	public boolean groupRemove(String string, String string1, String string2)
	{
		if(API != null)
			return API.groupRemove(string, string1, string2);
		return false;
	}
	@Override
	public boolean playerInGroup(String string, String string1, String string2)
	{
		if(API != null)
			return API.playerInGroup(string, string1, string2);
		return false;
	}
	@Override
	public boolean playerAddGroup(String string, String string1, String string2)
	{
		if(API != null)
			return API.playerAddGroup(string, string1, string2);
		return false;
	}
	@Override
	public boolean playerRemoveGroup(String string, String string1, String string2)
	{
		if(API != null)
			return API.playerRemoveGroup(string, string1, string2);
		return false;
	}
	@Override
	public String[] getPlayerGroups(String string, String string1)
	{
		if(API != null)
			return API.getPlayerGroups(string, string1);
		return new String[] { "Default" };
	}
	@Override
	public String getPrimaryGroup(String string, String string1)
	{
		if(API != null)
			return API.getPrimaryGroup(string, string1);
		return "Default";
	}
	@Override
	public String[] getGroups()
	{
		if(API != null)
			return API.getGroups();
		return new String[] { "Default" }; 
	}
}
