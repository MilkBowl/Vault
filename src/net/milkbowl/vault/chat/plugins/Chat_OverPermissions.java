package net.milkbowl.vault.chat.plugins;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.server.*;
import org.bukkit.plugin.Plugin;

import com.overmc.overpermissions.*;

public class Chat_OverPermissions extends Chat {

	protected final Plugin plugin;
	private OverPermissions overPerms;
	private OverPermissionsAPI api;

	public Chat_OverPermissions(Plugin plugin, Permission perms)
	{
		super(perms);
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

		if (overPerms == null) {
			Plugin p = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
			if (p != null) {
				overPerms = (OverPermissions) p;
				plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", new Object[] {plugin.getDescription().getName(), "OverPermissions"}));
			}
		}
		if ((api == null) && (overPerms != null)) {
			api = overPerms.getAPI();
		}
	}

	@Override
	public String getName( )
	{
		return "OverPermissions";
	}

	@Override
	public boolean isEnabled( )
	{
		return overPerms != null;
	}

	@Override
	public String getPlayerPrefix(String world, String player)
	{
		return getPlayerInfoString(world, player, "prefix", "");
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix)
	{
		setPlayerInfoString(world, player, "prefix", prefix);
	}

	@Override
	public String getPlayerSuffix(String world, String player)
	{
		return getPlayerInfoString(world, player, "suffix", "");
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix)
	{
		setPlayerInfoString(world, player, "suffix", suffix);
	}

	@Override
	public String getGroupPrefix(String world, String group)
	{
		return getGroupInfoString(world, group, "prefix", "");
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix)
	{
		setGroupInfoString(world, group, "prefix", prefix);
	}

	@Override
	public String getGroupSuffix(String world, String group)
	{
		return getGroupInfoString(world, group, "suffix", "");
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix)
	{
		setGroupInfoString(world, group, "prefix", suffix);
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
	{
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		try
		{
			return Integer.valueOf(s).intValue();
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value)
	{
		setPlayerInfoString(world, player, node, String.valueOf(value));
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
	{
		String s = getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		try
		{
			return Integer.valueOf(s).intValue();
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value)
	{
		setGroupInfoString(world, group, node, String.valueOf(value));
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
	{
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		try
		{
			return Double.valueOf(s).doubleValue();
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value)
	{
		setPlayerInfoString(world, player, node, String.valueOf(value));
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
	{
		String s = getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		try
		{
			return Double.valueOf(s).doubleValue();
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value)
	{
		setGroupInfoString(world, group, node, String.valueOf(value));
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
	{
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		Boolean val = Boolean.valueOf(s);
		return val != null ? val.booleanValue() : defaultValue;
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
	{
		setPlayerInfoString(world, player, node, String.valueOf(value));
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
	{
		String s = getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		Boolean val = Boolean.valueOf(s);
		return val != null ? val.booleanValue() : defaultValue;
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value)
	{
		setGroupInfoString(world, group, node, String.valueOf(value));
	}

	@Override
	public String getPlayerInfoString(String world, String playerName, String node, String defaultValue)
	{
		Player p = Bukkit.getPlayerExact(playerName);
		String ret = null;
		if (p != null) {
			ret = overPerms.getPlayerPermissions(p).getStringMeta(node, defaultValue);
		} else {
			int playerId = overPerms.getSQLManager().getPlayerId(playerName);
			int worldId = overPerms.getSQLManager().getWorldId(world);
			ret = overPerms.getSQLManager().getPlayerMetaValue(playerId, worldId, node);
		}
		if (ret == null) {
			return defaultValue;
		}
		return ret;
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value)
	{
		Player p = Bukkit.getPlayerExact(player);
		int playerId = overPerms.getSQLManager().getPlayerId(player, true);
		int worldId = overPerms.getSQLManager().getWorldId(player, false);
		if (worldId < 0) {
			overPerms.getSQLManager().setGlobalPlayerMeta(playerId, node, value);
		} else {
			overPerms.getSQLManager().setPlayerMeta(playerId, worldId, node, value);
		}
		if (p != null) {
			overPerms.getPlayerPermissions(p).recalculateMeta();
		}
	}

	@Override
	public String getGroupInfoString(String world, String groupName, String node, String defaultValue)
	{
		Group group = overPerms.getGroupManager().getGroup(groupName);
		if (group == null) {
			return defaultValue;
		}
		String value = group.getMeta(node);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	@Override
	public void setGroupInfoString(String world, String groupName, String node, String value)
	{
		Group group = overPerms.getGroupManager().getGroup(groupName);
		if (group == null) {
			return;
		}
		group.setMeta(node, value);
		group.recalculatePermissions();
	}

	public class PermissionServerListener
			implements Listener
	{
		Chat_OverPermissions chat = null;

		public PermissionServerListener(Chat_OverPermissions chat) {
			this.chat = chat;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (chat.overPerms == null) {
				Plugin chat = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
				if (chat != null) {
					this.chat.overPerms = (OverPermissions) chat;
					plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", new Object[] {plugin.getDescription().getName(), getName()}));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if ((chat.overPerms != null) &&
					(event.getPlugin().getDescription().getName().equals("OverPermissions"))) {
				chat.overPerms = null;
				plugin.getLogger().info(String.format("[%s][Chat] %s un-hooked.", new Object[] {plugin.getDescription().getName(), getName()}));
			}
		}
	}
}