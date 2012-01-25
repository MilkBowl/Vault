package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.herocraftonline.herotitles.HeroTitles;
import com.herocraftonline.herotitles.PlayerTitleManager;

public class Chat_HeroTitles extends Chat {

    private final Logger log = Logger.getLogger("Minecraft");
    private final String name = "HeroTitles";
    private HeroTitles chat;
    private Plugin plugin = null;

    public Chat_HeroTitles(Plugin plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (this.chat == null) {
            Plugin chat = plugin.getServer().getPluginManager().getPlugin("HeroTitles");
            if (chat != null) {
                this.chat = (HeroTitles) chat;
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "HeroTitles"));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Chat_HeroTitles chat = null;

        public PermissionServerListener(Chat_HeroTitles chat) {
            this.chat = chat;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (this.chat.chat == null) {
                Plugin chat = plugin.getServer().getPluginManager().getPlugin("HeroTitles");
                if (chat != null) {
                    this.chat.chat = (HeroTitles) chat;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "HeroTitles"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
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
        if (PlayerTitleManager.getTitlePrefix(player) != null) {
            return PlayerTitleManager.getTitlePrefix(player).data;
        }
        return "";
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        PlayerTitleManager.setTitlePrefix(player, prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        if (PlayerTitleManager.getTitleSuffix(player) != null) {
            return PlayerTitleManager.getTitleSuffix(player).data;
        }
        return "";
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        PlayerTitleManager.setTitleSuffix(player, suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");

    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");

    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        if (node.equals("color"))
            return PlayerTitleManager.getTitleColor(player) != null ? PlayerTitleManager.getTitleColor(player).toString() : "";

        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        throw new UnsupportedOperationException("HeroTitles does not support info nodes!");
    }
}
