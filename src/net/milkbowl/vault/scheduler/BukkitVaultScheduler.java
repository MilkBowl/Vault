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

    Added by YoannFM (2026) as part of Folia compatibility support.
 */
package net.milkbowl.vault.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * {@link VaultScheduler} backed by the classic Bukkit scheduler (Bukkit, Spigot, Paper).
 */
final class BukkitVaultScheduler implements VaultScheduler {

    private final Plugin plugin;

    BukkitVaultScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runGlobal(Runnable task) {
        scheduler().runTask(plugin, task);
    }

    @Override
    public void runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        scheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    @Override
    public void cancelAll() {
        scheduler().cancelTasks(plugin);
    }

    private BukkitScheduler scheduler() {
        return plugin.getServer().getScheduler();
    }
}
