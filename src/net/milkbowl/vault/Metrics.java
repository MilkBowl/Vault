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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Tooling to post to metrics.griefcraft.com
 */
public class Metrics {

    /**
     * The metrics revision number
     */
    private final static int REVISION = 3;

    /**
     * The base url of the metrics domain
     */
    private static final String BASE_URL = "http://metrics.griefcraft.com";

    /**
     * The url used to report a server's status
     */
    private static final String REPORT_URL = "/report/%s";

    /**
     * The file where guid and opt out is stored in
     */
    private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";

    /**
     * Interval of time to ping in minutes
     */
    private final static int PING_INTERVAL = 10;

    /**
     * The plugin configuration file
     */
    private final YamlConfiguration configuration;

    /**
     * Unique server id
     */
    private String guid;


    private final String pluginVersion;

    public Metrics(String version) throws IOException {
        this.pluginVersion = version;

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
     * Begin measuring a plugin
     *
     * @param plugin
     */
    public void beginMeasuringPlugin(final Plugin plugin) throws IOException {
        // Did we opt out?
        if (configuration.getBoolean("opt-out", false)) {
            return;
        }

        // First tell the server about us
        postPlugin(plugin, false);

        // Ping the server in intervals
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                try {
                    postPlugin(plugin, true);
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
        String response = "ERR No response";
        String data = encode("guid") + '=' + encode(guid)
                + '&' + encode("version") + '=' + encode(pluginVersion)
                + '&' + encode("server") + '=' + encode(Bukkit.getVersion())
                + '&' + encode("players") + '=' + encode(String.valueOf(Bukkit.getServer().getOnlinePlayers().length))
                + '&' + encode("revision") + '=' + encode(REVISION + "");

        // If we're pinging, append it
        if (isPing) {
            data += '&' + encode("ping") + '=' + encode("true");
        }

        // Create the url
        URL url = new URL(BASE_URL + String.format(REPORT_URL, "Vault"));

        // Connect to the website
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);

        // Write the data
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();

        // Now read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = reader.readLine();

        // close resources
        writer.close();
        reader.close();

        if (response.startsWith("ERR")){
            throw new IOException(response); //Throw the exception
        }
        //if (response.startsWith("OK")) - We should get "OK" followed by an optional description if everything goes right
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

}