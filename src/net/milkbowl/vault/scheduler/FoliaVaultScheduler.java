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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * {@link VaultScheduler} backed by Folia's GlobalRegionScheduler and AsyncScheduler.
 * <p>
 * Vault is compiled against the legacy Bukkit API so that it keeps running on
 * older servers. Folia's scheduler types are therefore not available at compile
 * time and are resolved once, by reflection, when this scheduler is created.
 */
final class FoliaVaultScheduler implements VaultScheduler {

    private static final String REGIONIZED_SERVER_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
    private static final String GLOBAL_SCHEDULER_CLASS = "io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler";
    private static final String ASYNC_SCHEDULER_CLASS = "io.papermc.paper.threadedregions.scheduler.AsyncScheduler";
    private static final long MILLIS_PER_TICK = 50L;

    private final Plugin plugin;
    private final Object globalScheduler;
    private final Object asyncScheduler;
    private final Method globalExecute;
    private final Method globalCancelTasks;
    private final Method asyncRunAtFixedRate;
    private final Method asyncCancelTasks;

    FoliaVaultScheduler(Plugin plugin) {
        this.plugin = plugin;
        try {
            Server server = plugin.getServer();
            Class<?> globalType = Class.forName(GLOBAL_SCHEDULER_CLASS);
            Class<?> asyncType = Class.forName(ASYNC_SCHEDULER_CLASS);

            globalScheduler = Server.class.getMethod("getGlobalRegionScheduler").invoke(server);
            asyncScheduler = Server.class.getMethod("getAsyncScheduler").invoke(server);
            globalExecute = globalType.getMethod("execute", Plugin.class, Runnable.class);
            globalCancelTasks = globalType.getMethod("cancelTasks", Plugin.class);
            asyncRunAtFixedRate = asyncType.getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);
            asyncCancelTasks = asyncType.getMethod("cancelTasks", Plugin.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to access the Folia scheduler API", e);
        }
    }

    /**
     * @return true if the running server is Folia (or a Folia fork)
     */
    static boolean isSupported() {
        try {
            Class.forName(REGIONIZED_SERVER_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void runGlobal(Runnable task) {
        invoke(globalExecute, globalScheduler, plugin, task);
    }

    @Override
    public void runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        Consumer<Object> scheduledTask = ignoredHandle -> task.run();
        invoke(asyncRunAtFixedRate, asyncScheduler, plugin, scheduledTask,
                delayTicks * MILLIS_PER_TICK, periodTicks * MILLIS_PER_TICK, TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancelAll() {
        invoke(globalCancelTasks, globalScheduler, plugin);
        invoke(asyncCancelTasks, asyncScheduler, plugin);
    }

    private static void invoke(Method method, Object target, Object... args) {
        try {
            method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException(cause);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
