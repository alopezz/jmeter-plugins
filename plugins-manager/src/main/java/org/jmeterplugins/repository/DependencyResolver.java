package org.jmeterplugins.repository;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyResolver {
    private static final Logger log = LoggingManager.getLoggerForClass();
    public static final String JMETER = "jmeter";
    protected Set<Plugin> deletions = new HashSet<>();
    protected Set<Plugin> additions = new HashSet<>();
    protected final Map<Plugin, Boolean> allPlugins;

    public DependencyResolver(Map<Plugin, Boolean> allPlugins) {
        this.allPlugins = allPlugins;

        resolveFlags();
        resolveUpgrades();
        resolveDeleteByDependency();
        resolveInstallByDependency();
        // TODO resolveDeleteLibs();
        // TODO resolveInstallLibs();
    }

    public Set<Plugin> getDeletions() {
        return deletions;
    }

    public Set<Plugin> getAdditions() {
        return additions;
    }

    private Plugin getPluginByID(String id) {
        for (Plugin plugin : allPlugins.keySet()) {
            if (plugin.getID().equals(id)) {
                return plugin;
            }
        }
        throw new RuntimeException("Plugin not found by ID: " + id);
    }

    private Set<Plugin> getDependants(Plugin plugin) {
        Set<Plugin> res = new HashSet<>();
        for (Plugin pAll : allPlugins.keySet()) {
            for (String depID : pAll.getDepends()) {
                if (depID.equals(plugin.getID())) {
                    res.add(pAll);
                }
            }
        }
        return res;
    }

    private void resolveFlags() {
        for (Map.Entry<Plugin, Boolean> entry : allPlugins.entrySet()) {
            if (entry.getKey().isInstalled()) {
                if (!entry.getValue()) {
                    deletions.add(entry.getKey());
                }
            } else if (entry.getValue()) {
                additions.add(entry.getKey());
            }
        }
    }

    private void resolveUpgrades() {
        // detect upgrades
        for (Map.Entry<Plugin, Boolean> entry : allPlugins.entrySet()) {
            Plugin plugin = entry.getKey();
            if (entry.getValue() && plugin.isInstalled() && !plugin.getInstalledVersion().equals(plugin.getCandidateVersion())) {
                deletions.add(plugin);
                additions.add(plugin);
            }
        }
    }

    private void resolveDeleteByDependency() {
        // delete by depend
        boolean hasModifications = true;
        while (hasModifications) {
            log.debug("Check uninstall dependencies");
            hasModifications = false;
            for (Plugin plugin : deletions) {
                if (!additions.contains(plugin)) {
                    for (Plugin dep : getDependants(plugin)) {
                        if (!deletions.contains(dep) && dep.isInstalled()) {
                            deletions.add(dep);
                            hasModifications = true;
                        }
                        if (additions.contains(dep)) {
                            additions.remove(dep);
                            hasModifications = true;
                        }
                    }
                }
            }
        }
    }

    private void resolveInstallByDependency() {
        // resolve dependencies
        boolean hasModifications = true;
        while (hasModifications) {
            log.debug("Check install dependencies: " + additions);
            hasModifications = false;
            for (Plugin plugin : additions) {
                for (String pluginID : plugin.getDepends()) {
                    if (pluginID.equals(JMETER)) {
                        // TODO: special check for jmeter ver
                        log.debug("Special case for JMeter core");
                        continue;
                    }

                    Plugin depend = getPluginByID(pluginID);
                    if (!additions.contains(depend)) {
                        log.debug("Add to install: " + depend);
                        additions.add(depend);
                        hasModifications = true;
                    }
                }

                if (hasModifications) {
                    break; // prevent ConcurrentModificationException
                }
            }
        }
    }


}
