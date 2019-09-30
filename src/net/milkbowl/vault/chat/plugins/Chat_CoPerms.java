package net.milkbowl.vault.chat.plugins;

import com.njdaeger.coperms.CoPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class Chat_CoPerms extends Chat {

    private static final String NAME = "CoPerms";
    private final Logger logger;
    private CoPerms coperms;

    public Chat_CoPerms(Plugin plugin, Permission perms) {
        super(perms);
        this.logger = plugin.getLogger();
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServiceListener(this), plugin);
    }

    public class PermissionServiceListener implements Listener {

        Chat_CoPerms chat;

        public PermissionServiceListener(Chat_CoPerms chat) {
            this.chat = chat;
        }

        @EventHandler( priority = EventPriority.MONITOR )
        public void onEnable(PluginEnableEvent e) {
            if (chat.coperms == null && e.getPlugin() instanceof CoPerms) {
                chat.coperms = (CoPerms)e.getPlugin();
                logger.info(String.format("[Chat] %s hooked.", NAME));
            }
        }

        @EventHandler( priority = EventPriority.MONITOR )
        public void onDisable(PluginDisableEvent e) {
            if (chat.coperms != null && e.getPlugin() instanceof CoPerms) {
                logger.info(String.format("[Chat] %s un-hooked.", NAME));
                chat.coperms = null;
            }
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isEnabled() {
        return coperms != null && coperms.isEnabled();
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return coperms.getUser(world, player).getPrefix();
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        coperms.getUser(world, player).setPrefix(prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return coperms.getUser(world, player).getSuffix();
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        coperms.getUser(world, player).setSuffix(suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return coperms.getGroup(world, group).getPrefix();
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        coperms.getGroup(world, group).setPrefix(prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return coperms.getGroup(world, group).getSuffix();
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        coperms.getGroup(world, group).setSuffix(suffix);
    }

    //
    //
    //
    private Object getPlayerInfo(String world, String player, String node) {
        return coperms.getUser(world, player).getInfo(node);
    }

    private void setPlayerInfo(String world, String player, String node, Object val) {
        coperms.getUser(world, player).addInfo(node, val);
    }

    private Object getGroupInfo(String world, String group, String node) {
        return coperms.getGroup(world, group).getInfo(node);
    }

    private void setGroupInfo(String world, String group, String node, Object val) {
        coperms.getGroup(world, group).addInfo(node, val);
    }
    //
    //
    //

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defVal) {
        return getPlayerInfo(world, player, node) == null ? defVal : (int)getPlayerInfo(world, player, node);
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defVal) {
        return getGroupInfo(world, group, node) == null ? defVal : (int)getGroupInfo(world, group, node);
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        setGroupInfo(world, group, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defVal) {
        return getPlayerInfo(world, player, node) == null ? defVal : (double)getPlayerInfo(world, player, node);
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defVal) {
        return getGroupInfo(world, group, node) == null ? defVal : (double)getGroupInfo(world, group, node);
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        setGroupInfo(world, group, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defVal) {
        return getPlayerInfo(world, player, node) == null ? defVal : (boolean)getPlayerInfo(world, player, node);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defVal) {
        return getGroupInfo(world, group, node) == null ? defVal : (boolean)getGroupInfo(world, group, node);
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        setGroupInfo(world, group, node, value);
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defVal) {
        return getPlayerInfo(world, player, node) == null ? defVal : (String)getPlayerInfo(world, player, node);
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defVal) {
        return getGroupInfo(world, group, node) == null ? defVal : (String)getGroupInfo(world, group, node);
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        setGroupInfo(world, group, node, value);
    }
}
