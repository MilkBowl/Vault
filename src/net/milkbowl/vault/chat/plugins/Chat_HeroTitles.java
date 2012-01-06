package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.herocraftonline.herotitles.HeroTitles;
import com.herocraftonline.herotitles.PlayerTitleManager;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class Chat_HeroTitles extends Chat {

    private final Logger log = Logger.getLogger("Minecraft");
    private final String name = "HeroTitles";
    private PermissionServerListener permissionServerListener;
    private Chat subChat;
    private HeroTitles chat;
    private Plugin plugin = null;

    public Chat_HeroTitles(Plugin plugin, Permission perms, Chat subChat) {
        super(perms);
        this.plugin = plugin;
        this.subChat = subChat;
        PluginManager pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (this.chat == null) {
            Plugin chat = plugin.getServer().getPluginManager().getPlugin("HeroTitles");
            if (chat != null) {
                this.chat = (HeroTitles) chat;
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "HeroTitles"));
            }
        }
    }

    private class PermissionServerListener extends ServerListener {
        Chat_HeroTitles chat = null;

        public PermissionServerListener(Chat_HeroTitles chat) {
            this.chat = chat;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (this.chat.chat == null) {
                Plugin chat = plugin.getServer().getPluginManager().getPlugin("HeroTitles");
                if (chat != null) {
                    this.chat.chat = (HeroTitles) chat;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "HeroTitles"));
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (this.chat.chat != null) {
                if (event.getPlugin().getDescription().getName().equals("HeroTitles")) {
                    this.chat.chat = null;
                    log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "HeroTitles"));
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
        return chat != null && chat.isEnabled();
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return PlayerTitleManager.getTitlePrefix(player).data;
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        PlayerTitleManager.setTitlePrefix(player, prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return PlayerTitleManager.getTitleSuffix(player).data;
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        PlayerTitleManager.setTitleSuffix(player, suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        if (subChat != null)
            return subChat.getGroupPrefix(world, group);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        if (subChat != null)
            subChat.setGroupPrefix(world, group, prefix);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        if (subChat != null)
            return subChat.getGroupSuffix(world, group);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        if (subChat != null)
            subChat.setGroupSuffix(world, group, suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        if (subChat != null)
            return subChat.getPlayerInfoInteger(world, player, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        if (subChat != null)
            subChat.setPlayerInfoInteger(world, player, node, value);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        if (subChat != null)
            return subChat.getGroupInfoInteger(world, group, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        if (subChat != null)
            subChat.setGroupInfoInteger(world, group, node, value);
        
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        if (subChat != null)
            return subChat.getPlayerInfoDouble(world, player, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        if (subChat != null)
            subChat.setPlayerInfoDouble(world, player, node, value);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");

    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        if (subChat != null)
            return subChat.getGroupInfoDouble(world, group, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        if (subChat != null)
            subChat.setGroupInfoDouble(world, group, node, value);
        
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        if (subChat != null)
            return subChat.getPlayerInfoBoolean(world, player, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        if (subChat != null)
            subChat.setPlayerInfoBoolean(world, player, node, value);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");

    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        if (subChat != null)
            return subChat.getGroupInfoBoolean(world, group, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        if (subChat != null)
            subChat.setGroupInfoBoolean(world, group, node, value);
        
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        if (node.equals("color"))
            return PlayerTitleManager.getTitleColor(player).toString();

        if (subChat != null)
            return subChat.getPlayerInfoString(world, player, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        if (subChat != null)
            subChat.setPlayerInfoString(world, player, node, value);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        if (subChat != null)
            return subChat.getGroupInfoString(world, group, node, defaultValue);

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        if (subChat != null)
            subChat.setGroupInfoString(world, group, node, value);
        
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }
}
