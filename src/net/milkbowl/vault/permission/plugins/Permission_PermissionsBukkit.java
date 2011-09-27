package net.milkbowl.vault.permission.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

import net.D3GN.MiracleM4n.mChat.mChatAPI;
import net.milkbowl.vault.permission.Permission;

public class Permission_PermissionsBukkit extends Permission {
	private static final Logger log = Logger.getLogger("Minecraft");

	private String name = "PermissionsBukkit";
	private Plugin plugin = null;
	private PluginManager pluginManager = null;
	private PermissionsPlugin perms = null;
	private mChatAPI mChat = null;
	private PermissionServerListener permissionServerListener = null;
	private ConsoleCommandSender ccs;

	public Permission_PermissionsBukkit(Plugin plugin) {
		this.plugin = plugin;
		ccs = new ConsoleCommandSender(plugin.getServer());
		pluginManager = this.plugin.getServer().getPluginManager();

		permissionServerListener = new PermissionServerListener(this);

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

		// Load Plugin in case it was loaded before
		if (perms == null) {
			Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
			if (perms != null) {
				perms = (PermissionsPlugin) perms;
				log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
		
		if (mChat == null) {
			Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
			if (chat != null) {
				mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
				log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
			}
		}
	}


	private class PermissionServerListener extends ServerListener {
		Permission_PermissionsBukkit permission = null;

		public PermissionServerListener(Permission_PermissionsBukkit permission) {
			this.permission = permission;
		}

		public void onPluginEnable(PluginEnableEvent event) {
			if (permission.perms == null) {
				Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");

				if (perms != null) {
					if (perms.isEnabled()) {
						permission.perms = (PermissionsPlugin) perms;
						log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
					}
				}
			}
			if (mChat == null) {
				Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
				if (chat != null) {
					mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
					log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
				}
			}
		}

		public void onPluginDisable(PluginDisableEvent event) {
			if (permission.perms != null) {
				if (event.getPlugin().getDescription().getName().equals("PermissionsBukkit")) {
					permission.perms = null;
					log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
				}
			}
			if (mChat != null) {
				if (event.getPlugin().getDescription().getName().equals("mChat")) {
					mChat = null;
					log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "mChat"));
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
		if (perms == null) {
			return false;
		} else {
			return perms.isEnabled();
		}
	}

	@Override
	public boolean playerHas(String world, String player, String permission) {
		if (plugin.getServer().getPlayer(player) != null)
			return plugin.getServer().getPlayer(player).hasPermission(permission);
		else
			return false;
	}

	@Override
	public boolean playerAdd(String world, String player, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(ccs, "permission player setperm " + player + " " + permission + " true");
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
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(ccs, "permission player unsetperm " + player + " " + permission);
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
		if (world != null && !world.isEmpty()) {
			return perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission) == null ? false : perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission);
		}
		if (perms.getGroup(group) == null)
			return false;
		else if (perms.getGroup(group).getInfo() == null)
			return false;
		else if (perms.getGroup(group).getInfo().getPermissions() == null)
			return false;
		return perms.getGroup(group).getInfo().getPermissions().get(permission);
	}

	@Override
	public boolean groupAdd(String world, String group, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(ccs, "permission group setperm " + group + " " + permission + " true");
	}

	@Override
	public boolean groupRemove(String world, String group, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(ccs, "permission group unsetperm " + group + " " + permission);
	}

	@Override
	public boolean playerInGroup(String world, String player, String group) {
		if (world != null) {
			for (Group g : perms.getPlayerInfo(player).getGroups()) {
				if (g.getName().equals(group)) {
					return g.getInfo().getWorlds().contains(world);
				}
			}
			return false;
		}
		return perms.getGroup(group).getPlayers().contains(player);
	}

	@Override
	public boolean playerAddGroup(String world, String player, String group) {
		if (world != null) {
			throw new UnsupportedOperationException(getName() + " does not support world based groups.");
		}
		return plugin.getServer().dispatchCommand(ccs, "permission player addgroup " + group + " " + player);
	}

	@Override
	public boolean playerRemoveGroup(String world, String player, String group) {
		if (world != null) {
			throw new UnsupportedOperationException(getName() + " does not support world based groups.");
		}
		return plugin.getServer().dispatchCommand(ccs, "permission player removegroup " + group + " " + player);
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node,int value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");

	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
		return this.groupHas(world, group, node);
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
		throw new UnsupportedOperationException(getName() + " does not support info nodes.");
	}

	@Override
	public String[] getPlayerGroups(String world, String player) {
		List<String> groupList = new ArrayList<String>();
		if (world != null && perms.getPlayerInfo(player) != null) {
			for (Group group : perms.getPlayerInfo(player).getGroups()) {
				if (group.getInfo().getWorlds().contains(world)) {
					groupList.add(group.getName());
				}
			}
			return groupList.toArray(new String[0]);
		}
		for (Group group : perms.getPlayerInfo(player).getGroups()) {
			groupList.add(group.getName());
		}
		return groupList.toArray(new String[0]);
	}

	@Override
	public String getPrimaryGroup(String world, String player) {
		if (perms.getPlayerInfo(player) == null)
			return null;
		else if (perms.getPlayerInfo(player).getGroups() != null ) {
			return perms.getPlayerInfo(player).getGroups().get(0).getName();
		}
		return null;
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
		if (mChat == null) {
			throw new UnsupportedOperationException(getName() + " does not support info nodes.");
		} else if (plugin.getServer().getPlayer(player) == null){
			throw new UnsupportedOperationException(getName() + " does not support offline node retrieval");
		} else {
			return mChat.getPrefix(plugin.getServer().getPlayer(player));
		}
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {

	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		if (mChat == null) {
			throw new UnsupportedOperationException(getName() + " does not support info nodes.");
		} else if (plugin.getServer().getPlayer(player) == null){
			throw new UnsupportedOperationException(getName() + " does not support offline node retrieval");
		} else {
			return mChat.getSuffix(plugin.getServer().getPlayer(player));
		}
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getGroupPrefix(String world, String group) {
		throw new UnsupportedOperationException(getName() + " does not support group info nodes.");
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		throw new UnsupportedOperationException(getName() + " does not support group info nodes.");
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		// TODO Auto-generated method stub

	}
}
