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

/**
 * Platform independent task scheduling used internally by Vault.
 * Hides the differences between the classic Bukkit scheduler and the
 * region based schedulers of Folia.
 */
public interface VaultScheduler {

    /**
     * Runs a task on the thread owning global server state
     * (main thread on Bukkit, global region thread on Folia).
     */
    void runGlobal(Runnable task);

    /**
     * Repeatedly runs a task off the server threads.
     */
    void runAsyncTimer(Runnable task, long delayTicks, long periodTicks);

    /**
     * Cancels every task scheduled by this scheduler's plugin.
     */
    void cancelAll();

    /**
     * Creates the scheduler matching the running server implementation.
     */
    static VaultScheduler create(Plugin plugin) {
        return FoliaVaultScheduler.isSupported() ? new FoliaVaultScheduler(plugin) : new BukkitVaultScheduler(plugin);
    }
}
