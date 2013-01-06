package net.milkbowl.vault;

/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Tooling to post to metrics.griefcraft.com
 */
public class Metrics {

    /**
     * The metrics revision number
     */
    private final static int REVISION = 5;

    /**
     * The base url of the metrics domain
     */
    private static final String BASE_URL = "http://metrics.griefcraft.com";

    /**
     * The url used to report a server's status
     */
    private static final String REPORT_URL = "/report/%s";

    /**
     * The separator to use for custom data. This MUST NOT change unless you are hosting your own
     * version of metrics and want to change it.
     */
    private static final String CUSTOM_DATA_SEPARATOR = "~~";

    /**
     * The file where guid and opt out is stored in
     */
    private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";

    /**
     * Interval of time to ping in minutes
     */
    private final static int PING_INTERVAL = 10;

    /**
     * A map of all of the graphs for each plugin
     */
    private Map<Plugin, Set<Graph>> graphs = Collections.synchronizedMap(new HashMap<Plugin, Set<Graph>>());

    /**
     * A convenient map of the default Graph objects (used by addCustomData mainly)
     */
    private Map<Plugin, Graph> defaultGraphs = Collections.synchronizedMap(new HashMap<Plugin, Graph>());

    /**
     * The plugin configuration file
     */
    private final YamlConfiguration configuration;

    /**
     * Unique server id
     */
    private String guid;

    private final String pluginVersion;
    private final String authors;

    public Metrics(String version, String authors) throws IOException {
        this.pluginVersion = version;
        this.authors = authors;

        // load the config
        File file = new File(CONFIG_FILE);
        configuration = YamlConfiguration.loadConfiguration(file);

        // add some defaults
        configuration.addDefault("opt-out", false);
        configuration.addDefault("guid", UUID.randomUUID().toString());

        // Do we need to create the file?
        if (configuration.get("guid", null) == null) {
            configuration.options().header("http://metrics.griefcraft.com").copyDefaults(true);
            configuration.save(file);
        }

        // Load the guid then
        guid = configuration.getString("guid");
    }
    /**
     * Construct and create a Graph that can be used to separate specific plotters to their own graphs
     * on the metrics website. Plotters can be added to the graph object returned.
     *
     * @param plugin
     * @param type
     * @param name
     * @return Graph object created. Will never return NULL under normal circumstances unless bad parameters are given
     */
    public Graph createGraph(Plugin plugin, Graph.Type type, String name) {
        if (plugin == null || type == null || name == null) {
            throw new IllegalArgumentException("All arguments must not be null");
        }

        // Construct the graph object
        Graph graph = new Graph(type, name);

        // Get the graphs for the plugin
        Set<Graph> graphs = getOrCreateGraphs(plugin);

        // Now we can add our graph
        graphs.add(graph);

        // and return back
        return graph;
    }

    public void findCustomData(Vault plugin) {
        // Create our Economy Graph and Add our Economy plotters
        Graph econGraph = createGraph(plugin, Graph.Type.Pie, "Economy");
        RegisteredServiceProvider<Economy> rspEcon = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rspEcon != null) {
            econ = rspEcon.getProvider();
        }
        final String econName = econ != null ? econ.getName() : "No Economy";
        econGraph.addPlotter(new Metrics.Plotter(econName) {

            @Override
            public int getValue() {
                return 1;
            }
        });

        // Create our Permission Graph and Add our permission Plotters
        Graph permGraph = createGraph(plugin, Graph.Type.Pie, "Permission");
        final String permName = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider().getName();
        permGraph.addPlotter(new Metrics.Plotter(permName) {

            @Override
            public int getValue() {
                return 1;
            }
        });

        // Create our Chat Graph and Add our chat Plotters
        Graph chatGraph = createGraph(plugin, Graph.Type.Pie, "Chat");
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        Chat chat = null;
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }
        final String chatName = chat != null ? chat.getName() : "No Chat";
        // Add our Chat Plotters
        chatGraph.addPlotter(new Metrics.Plotter(chatName) {

            @Override
            public int getValue() {
                return 1;
            }
        });
    }

    /**
     * Adds a custom data plotter for a given plugin
     *
     * @param plugin
     * @param plotter
     */
    public synchronized void addCustomData(Plugin plugin, Plotter plotter) {
        // The default graph for the plugin
        Graph graph = getOrCreateDefaultGraph(plugin);

        // Add the plotter to the graph o/
        graph.addPlotter(plotter);

        // Ensure the default graph is included in the submitted graphs
        getOrCreateGraphs(plugin).add(graph);
    }

    /**
     * Begin measuring a plugin
     *
     * @param plugin
     */
    public void beginMeasuringPlugin(final Plugin plugin) throws IOException {
        // Did we opt out?
        if (configuration.getBoolean("opt-out", false)) {
            return;
        }

        // Ping the server in intervals
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            private boolean firstPost = true;
            public void run() {
                try {
                    postPlugin(plugin, !firstPost);
                    firstPost = false;
                } catch (IOException e) {
                    //Ignore exceptions - otherwise we freak server admins out
                }
            }
        }, 0, PING_INTERVAL * 1200);

        return;
    }

    /**
     * Generic method that posts a plugin to the metrics website
     *
     * @param plugin
     */
    private void postPlugin(Plugin plugin, boolean isPing) throws IOException {
        // Construct the post data
        String data = encode("guid") + '=' + encode(guid)
                + encodeDataPair("authors", authors)
                + encodeDataPair("version", pluginVersion)
                + encodeDataPair("server", Bukkit.getVersion())
                + encodeDataPair("players", Integer.toString(Bukkit.getServer().getOnlinePlayers().length))
                + encodeDataPair("revision", String.valueOf(REVISION));

        // If we're pinging, append it
        if (isPing) {
            data += encodeDataPair("ping", "true");
        }

        // Add any custom data available for the plugin
        Set<Graph> graphs = getOrCreateGraphs(plugin);

        // Acquire a lock on the graphs, which lets us make the assumption we also lock everything
        // inside of the graph (e.g plotters)
        synchronized(graphs) {
            Iterator<Graph> iter = graphs.iterator();

            while (iter.hasNext()) {
                Graph graph = iter.next();

                // Because we have a lock on the graphs set already, it is reasonable to assume
                // that our lock transcends down to the individual plotters in the graphs also.
                // Because our methods are private, no one but us can reasonably access this list
                // without reflection so this is a safe assumption without adding more code.
                for (Plotter plotter : graph.getPlotters()) {
                    // The key name to send to the metrics server
                    // The format is C-GRAPHNAME-PLOTTERNAME where separator - is defined at the top
                    // Legacy (R4) submitters use the format Custom%s, or CustomPLOTTERNAME
                    String key = String.format("C%s%s%s%s", CUSTOM_DATA_SEPARATOR, graph.getName(), CUSTOM_DATA_SEPARATOR, plotter.getColumnName());

                    // The value to send, which for the foreseeable future is just the string
                    // value of plotter.getValue()
                    String value = Integer.toString(plotter.getValue());

                    // Add it to the http post data :)
                    data += encodeDataPair(key, value);
                }
            }
        }

        // Create the url
        URL url = new URL(BASE_URL + String.format(REPORT_URL, plugin.getDescription().getName()));

        // Connect to the website
        URLConnection connection;

        // Mineshafter creates a socks proxy, so we can safely bypass it
        // It does not reroute POST requests so we need to go around it
        if (isMineshafterPresent()) {
            connection = url.openConnection(Proxy.NO_PROXY);
        } else {
            connection = url.openConnection();
        }

        connection.setDoOutput(true);

        // Write the data
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();

        // Now read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.readLine();

        // close resources
        writer.close();
        reader.close();
        
        if (response == null || response.startsWith("ERR")) {
            throw new IOException(response); //Throw the exception
        } else {
            // Is this the first update this hour?
            if (response.contains("OK This is your first update this hour")) {
                synchronized (graphs) {
                    Iterator<Graph> iter = graphs.iterator();

                    while (iter.hasNext()) {
                        Graph graph = iter.next();

                        for (Plotter plotter : graph.getPlotters()) {
                            plotter.reset();
                        }
                    }
                }
            }
        }
        //if (response.startsWith("OK")) - We should get "OK" followed by an optional description if everything goes right
    }

    /**
     * Get or create the Set of graphs for a specific plugin
     *
     * @param plugin
     * @return
     */
    private Set<Graph> getOrCreateGraphs(Plugin plugin) {
        Set<Graph> theGraphs = graphs.get(plugin);

        // Create the Set if it does not already exist
        if (theGraphs == null) {
            theGraphs = Collections.synchronizedSet(new HashSet<Graph>());
            graphs.put(plugin, theGraphs);
        }

        return theGraphs;
    }

    /**
     * Get the default graph for a plugin and if it does not exist, create one
     *
     * @param plugin
     * @return
     */
    private Graph getOrCreateDefaultGraph(Plugin plugin) {
        Graph graph = defaultGraphs.get(plugin);

        // Not yet created :(
        if (graph == null) {
            graph = new Graph(Graph.Type.Line, "Default");
            defaultGraphs.put(plugin, graph);
        }

        return graph;
    }

    /**
     * Check if mineshafter is present. If it is, we need to bypass it to send POST requests
     *
     * @return
     */
    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Encode a key/value data pair to be used in a HTTP post request. This INCLUDES a & so the first
     * key/value pair MUST be included manually, e.g:
     * <p>
     *     String httpData = encode("guid") + "=" + encode("1234") + encodeDataPair("authors") + "..";
     * </p>
     *
     * @param key
     * @param value
     * @return
     */
    private static String encodeDataPair(String key, String value) throws UnsupportedEncodingException {
        return "&" + encode(key) + "=" + encode(value);
    }

    /**
     * Encode text as UTF-8
     *
     * @param text
     * @return
     */
    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }
    /**
     * Represents a custom graph on the website
     */
    public static class Graph {

        /**
         * The graph's type that will be visible on the website
         */
        public static enum Type {

            /**
             * A simple line graph which also includes a scrollable timeline viewer to view
             * as little or as much of the data as possible.
             */
            Line,

            /**
             * An area graph. This is the same as a line graph except the area under the curve is shaded
             */
            Area,

            /**
             * A column graph, which is a graph where the data is represented by columns on the vertical axis,
             * i.e they go up and down.
             */
            Column,

            /**
             * A pie graph. The graph is generated by taking the data for the last hour and summing it
             * together. Then the percentage for each plotter is calculated via round( (plot / total) * 100, 2 )
             */
            Pie

        }

        /**
         * What the graph should be plotted as
         */
        private final Type type;

        /**
         * The graph's name, alphanumeric and spaces only :)
         * If it does not comply to the above when submitted, it is rejected
         */
        private final String name;

        /**
         * The set of plotters that are contained within this graph
         */
        private final Set<Plotter> plotters = new LinkedHashSet<Plotter>();

        private Graph(Type type, String name) {
            this.type = type;
            this.name = name;
        }

        /**
         * Gets the graph's name
         *
         * @return name of graph
         */
        public String getName() {
            return name;
        }

        /**
         * Add a plotter to the graph, which will be used to plot entries
         *
         * @param plotter
         */
        public void addPlotter(Plotter plotter) {
            plotters.add(plotter);
        }

        /**
         * Remove a plotter from the graph
         *
         * @param plotter
         */
        public void removePlotter(Plotter plotter) {
            plotters.remove(plotter);
        }

        /**
         * Gets an <b>unmodifiable</b> set of the plotter objects in the graph
         * @return returns a Set of Plotter's
         */
        public Set<Plotter> getPlotters() {
            return Collections.unmodifiableSet(plotters);
        }

        @Override
        public int hashCode() {
            return (type.hashCode() * 17) ^ name.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Graph)) {
                return false;
            }

            Graph graph = (Graph) object;
            return graph.type == type && graph.name.equals(name);
        }

    }
    /**
     * Interface used to collect custom data for a plugin
     */
    public static abstract class Plotter {
        /**
         * The plot's name
         */
        private final String name;

        /*
         * Construct a plotter with the default plot name
         */
        public Plotter() {
            this("Default");
        }

        /**
         * Construct a plotter with a specific plot name
         *
         * @param name
         */
        public Plotter(String name) {
            this.name = name;
        }

        /**
         * Get the column name for the plotted point
         *
         * @return the plotted point's column name
         */
        public String getColumnName() {
            return name;
        }

        /**
         * Get the current value for the plotted point
         *
         * @return returns value
         */
        public abstract int getValue();

        @Override
        public int hashCode() {
            return getColumnName().hashCode() + getValue();
        }

        /**
         * Called after the website graphs have been updated
         */
        public void reset() {
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }

            Plotter plotter = (Plotter) object;
            return plotter.getColumnName().equals(getColumnName()) && plotter.getValue() == getValue();
        }

    }
}