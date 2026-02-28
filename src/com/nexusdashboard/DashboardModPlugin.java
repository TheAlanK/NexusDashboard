package com.nexusdashboard;

import com.fs.starfarer.api.BaseModPlugin;
import com.nexusui.overlay.NexusWebFrame;

import org.apache.log4j.Logger;

/**
 * NexusDashboard Mod Plugin.
 *
 * Registers the fleet dashboard page with NexusUI's overlay framework.
 * This mod demonstrates how to build on NexusUI as a consumer.
 */
public class DashboardModPlugin extends BaseModPlugin {

    private static final Logger log = Logger.getLogger(DashboardModPlugin.class);

    @Override
    public void onApplicationLoad() throws Exception {
        log.info("NexusDashboard: Loaded");
    }

    @Override
    public void onGameLoad(boolean newGame) {
        // Register our dashboard page with NexusUI
        NexusWebFrame.registerPage(new DashboardPage());
        log.info("NexusDashboard: Dashboard page registered with NexusUI");
    }
}
