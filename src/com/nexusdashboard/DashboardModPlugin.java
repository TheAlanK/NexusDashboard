package com.nexusdashboard;

import com.fs.starfarer.api.BaseModPlugin;
import com.nexusui.api.NexusPage;
import com.nexusui.api.NexusPageFactory;
import com.nexusui.overlay.NexusFrame;

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
        NexusFrame.registerPageFactory(new NexusPageFactory() {
            public String getId() { return "fleet_dashboard"; }
            public String getTitle() { return "Fleet Dashboard"; }
            public NexusPage create() { return new DashboardPage(); }
        });
        log.info("NexusDashboard: Dashboard page registered with NexusUI");
    }
}
