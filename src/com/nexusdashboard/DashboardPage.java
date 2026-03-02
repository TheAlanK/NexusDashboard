package com.nexusdashboard;

import com.nexusui.api.NexusPage;
import com.nexusui.bridge.GameDataBridge;
import com.nexusui.overlay.NexusFrame;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardPage - Fleet analytics dashboard built on NexusUI.
 *
 * Displays fleet composition, combat readiness, faction relations,
 * cargo overview, and colony data using NexusUI's rendering utilities.
 */
public class DashboardPage implements NexusPage {

    private DashboardPanel panel;

    // Cached API data
    private volatile JSONObject gameData = new JSONObject();
    private volatile JSONObject fleetData = new JSONObject();
    private volatile JSONObject coloniesData = new JSONObject();
    private volatile JSONObject cargoData = new JSONObject();
    private volatile JSONObject factionsData = new JSONObject();

    public String getId() { return "fleet_dashboard"; }
    public String getTitle() { return "Fleet Dashboard"; }

    public JPanel createPanel(int port) {
        panel = new DashboardPanel();
        return panel;
    }

    public void refresh() {
        // Read JSONObject references directly — zero serialization, zero parsing
        GameDataBridge bridge = GameDataBridge.getInstance();
        if (bridge == null) return;
        gameData = bridge.getGameInfoObj();
        fleetData = bridge.getFleetObj();
        coloniesData = bridge.getColoniesObj();
        cargoData = bridge.getCargoObj();
        factionsData = bridge.getFactionsObj();
    }

    // ========================================================================
    // Dashboard Panel - Custom painted dashboard
    // ========================================================================
    private class DashboardPanel extends JPanel {

        DashboardPanel() {
            setBackground(NexusFrame.BG_PRIMARY);
        }

        public Dimension getPreferredSize() {
            return new Dimension(940, 900);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int padding = 16;
            int cardGap = 12;
            int totalW = getWidth() - padding * 2;
            int halfW = (totalW - cardGap) / 2;
            int y = padding;

            // Header bar
            y = drawHeaderBar(g2, padding, y, totalW);
            y += cardGap;

            // Row 1: Fleet Composition | Combat Readiness
            int row1Y = y;
            int h1 = drawFleetCompositionCard(g2, padding, row1Y, halfW);
            int h2 = drawCombatReadinessCard(g2, padding + halfW + cardGap, row1Y, halfW);
            y += Math.max(h1, h2) + cardGap;

            // Row 2: Faction Relations | Cargo Hold
            int row2Y = y;
            int h3 = drawFactionsCard(g2, padding, row2Y, halfW);
            int h4 = drawCargoCard(g2, padding + halfW + cardGap, row2Y, halfW);
            y += Math.max(h3, h4) + cardGap;

            // Row 3: Colonies (full width)
            int h5 = drawColoniesCard(g2, padding, y, totalW);
            y += h5 + padding;

            // Update preferred size for scrolling
            Dimension pref = getPreferredSize();
            if (y > pref.height) {
                setPreferredSize(new Dimension(pref.width, y));
                revalidate();
            }

            g2.dispose();
        }

        // --- Header Bar ---
        private int drawHeaderBar(Graphics2D g2, int x, int y, int w) {
            int h = 50;
            NexusFrame.drawCardBg(g2, x, y, w, h);

            g2.setFont(NexusFrame.FONT_BODY);
            String name = gameData.optString("playerName", "---");
            g2.setColor(NexusFrame.TEXT_PRIMARY);
            g2.drawString(name, x + 16, y + 22);

            g2.setFont(NexusFrame.FONT_SMALL);
            g2.setColor(NexusFrame.TEXT_SECONDARY);
            g2.drawString(gameData.optString("faction", ""), x + 16, y + 38);

            // Right-aligned stats
            int rx = x + w - 16;
            g2.setFont(NexusFrame.FONT_MONO);

            String date = gameData.optString("dateString", "---");
            g2.setColor(NexusFrame.TEXT_SECONDARY);
            int dateW = g2.getFontMetrics().stringWidth(date);
            g2.drawString(date, rx - dateW, y + 22);

            long credits = gameData.optLong("credits", 0);
            String credStr = NexusFrame.formatNumber(credits) + " credits";
            g2.setColor(NexusFrame.ORANGE);
            int credW = g2.getFontMetrics().stringWidth(credStr);
            g2.drawString(credStr, rx - credW, y + 38);

            int ships = gameData.optInt("totalShips", 0);
            String shipStr = ships + " ships";
            g2.setColor(NexusFrame.CYAN);
            int shipW = g2.getFontMetrics().stringWidth(shipStr);
            g2.drawString(shipStr, rx - credW - shipW - 24, y + 38);

            return h;
        }

        // --- Fleet Composition Card ---
        private int drawFleetCompositionCard(Graphics2D g2, int x, int y, int w) {
            int headerH = 32;
            int bodyPad = 12;

            int capitals = fleetData.optInt("capitals", 0);
            int cruisers = fleetData.optInt("cruisers", 0);
            int destroyers = fleetData.optInt("destroyers", 0);
            int frigates = fleetData.optInt("frigates", 0);
            int fighters = fleetData.optInt("fighters", 0);
            int total = capitals + cruisers + destroyers + frigates + fighters;
            if (total == 0) total = 1;

            int barH = 22;
            int barGap = 6;
            int bodyH = (barH + barGap) * 5 + bodyPad * 2;
            int cardH = headerH + bodyH;

            NexusFrame.drawCardBg(g2, x, y, w, cardH);
            NexusFrame.drawCardHeader(g2, x, y, w, "FLEET COMPOSITION",
                fleetData.optInt("totalShips", 0) + " ships");

            int by = y + headerH + bodyPad;
            String[] labels = {"Capital", "Cruiser", "Destroyer", "Frigate", "Fighter"};
            int[] values = {capitals, cruisers, destroyers, frigates, fighters};
            Color[] colors = {NexusFrame.RED, NexusFrame.ORANGE, NexusFrame.YELLOW,
                              NexusFrame.CYAN, NexusFrame.PURPLE};

            for (int i = 0; i < labels.length; i++) {
                float pct = (float) values[i] / total;
                NexusFrame.drawLabeledBar(g2, x + bodyPad, by, w - bodyPad * 2, barH,
                    labels[i], String.valueOf(values[i]), pct, colors[i]);
                by += barH + barGap;
            }

            return cardH;
        }

        // --- Combat Readiness Card ---
        private int drawCombatReadinessCard(Graphics2D g2, int x, int y, int w) {
            int headerH = 32;
            int bodyPad = 12;
            int barH = 16;
            int barGap = 4;

            JSONArray members = fleetData.optJSONArray("members");
            int count = members != null ? Math.min(members.length(), 12) : 0;
            int bodyH = Math.max((barH + barGap) * count + bodyPad * 2, 60);
            int cardH = headerH + bodyH;

            NexusFrame.drawCardBg(g2, x, y, w, cardH);
            NexusFrame.drawCardHeader(g2, x, y, w, "COMBAT READINESS", "");

            int by = y + headerH + bodyPad;
            if (members != null) {
                for (int i = 0; i < count; i++) {
                    JSONObject m = members.optJSONObject(i);
                    if (m == null) continue;
                    String name = m.optString("shipName", m.optString("hullName", "?"));
                    if (name.isEmpty()) name = m.optString("hullName", "?");
                    int cr = m.optInt("cr", 0);
                    float pct = cr / 100f;
                    Color barColor = cr >= 70 ? NexusFrame.GREEN :
                                     cr >= 40 ? NexusFrame.ORANGE : NexusFrame.RED;
                    NexusFrame.drawLabeledBar(g2, x + bodyPad, by, w - bodyPad * 2, barH,
                        NexusFrame.truncate(name, 16), cr + "%", pct, barColor);
                    by += barH + barGap;
                }
            }

            return cardH;
        }

        // --- Factions Card ---
        private int drawFactionsCard(Graphics2D g2, int x, int y, int w) {
            int headerH = 32;
            int bodyPad = 12;
            int barH = 18;
            int barGap = 5;

            JSONArray factions = factionsData.optJSONArray("factions");
            int count = factions != null ? Math.min(factions.length(), 10) : 0;
            int bodyH = Math.max((barH + barGap) * count + bodyPad * 2, 60);
            int cardH = headerH + bodyH;

            NexusFrame.drawCardBg(g2, x, y, w, cardH);
            NexusFrame.drawCardHeader(g2, x, y, w, "FACTION RELATIONS", "");

            int by = y + headerH + bodyPad;
            if (factions != null) {
                for (int i = 0; i < count; i++) {
                    JSONObject f = factions.optJSONObject(i);
                    if (f == null) continue;
                    String name = f.optString("name", "?");
                    int rel = f.optInt("relation", 0);
                    Color c = rel >= 50 ? NexusFrame.GREEN :
                              rel >= 0 ? NexusFrame.CYAN :
                              rel >= -50 ? NexusFrame.ORANGE : NexusFrame.RED;
                    NexusFrame.drawRelationBar(g2, x + bodyPad, by, w - bodyPad * 2, barH,
                        name, rel, c);
                    by += barH + barGap;
                }
            }

            return cardH;
        }

        // --- Cargo Card ---
        private int drawCargoCard(Graphics2D g2, int x, int y, int w) {
            int headerH = 32;
            int bodyPad = 12;
            int lineH = 18;

            JSONArray commodities = cargoData.optJSONArray("commodities");
            int count = commodities != null ? commodities.length() : 0;
            int bodyH = Math.max(lineH * (count + 4) + bodyPad * 2, 60);
            int cardH = headerH + bodyH;

            NexusFrame.drawCardBg(g2, x, y, w, cardH);
            int spaceUsed = cargoData.optInt("spaceUsed", 0);
            int maxSpace = cargoData.optInt("maxSpace", 0);
            NexusFrame.drawCardHeader(g2, x, y, w, "CARGO HOLD", spaceUsed + "/" + maxSpace);

            int by = y + headerH + bodyPad;
            g2.setFont(NexusFrame.FONT_MONO);

            // Summary row
            String[] summaryLabels = {"Fuel", "Supplies", "Crew", "Marines"};
            String[] summaryKeys = {"fuel", "supplies", "crew", "marines"};
            Color[] summaryColors = {NexusFrame.ORANGE, NexusFrame.CYAN,
                                     NexusFrame.TEXT_PRIMARY, NexusFrame.RED};

            for (int i = 0; i < summaryLabels.length; i++) {
                int val = cargoData.optInt(summaryKeys[i], 0);
                g2.setColor(NexusFrame.TEXT_SECONDARY);
                g2.drawString(summaryLabels[i], x + bodyPad, by + 13);
                g2.setColor(summaryColors[i]);
                String valStr = NexusFrame.formatNumber(val);
                int valW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + w - bodyPad - valW, by + 13);
                by += lineH;
            }

            // Separator
            by += 4;
            g2.setColor(NexusFrame.BORDER);
            g2.drawLine(x + bodyPad, by, x + w - bodyPad, by);
            by += 8;

            // Commodities
            if (commodities != null) {
                for (int i = 0; i < commodities.length(); i++) {
                    JSONObject item = commodities.optJSONObject(i);
                    if (item == null) continue;
                    String id = item.optString("id", "?");
                    int qty = item.optInt("quantity", 0);
                    g2.setColor(NexusFrame.TEXT_SECONDARY);
                    g2.drawString(NexusFrame.prettifyId(id), x + bodyPad, by + 13);
                    g2.setColor(NexusFrame.TEXT_PRIMARY);
                    String qtyStr = NexusFrame.formatNumber(qty);
                    int qtyW = g2.getFontMetrics().stringWidth(qtyStr);
                    g2.drawString(qtyStr, x + w - bodyPad - qtyW, by + 13);
                    by += lineH;
                }
            }

            return cardH;
        }

        // --- Colonies Card ---
        private int drawColoniesCard(Graphics2D g2, int x, int y, int w) {
            int headerH = 32;
            int bodyPad = 12;
            int rowH = 28;

            JSONArray colonies = coloniesData.optJSONArray("colonies");
            int count = colonies != null ? colonies.length() : 0;
            int bodyH = Math.max(rowH * count + bodyPad * 2 + 20, 60);
            int cardH = headerH + bodyH;

            NexusFrame.drawCardBg(g2, x, y, w, cardH);
            long totalIncome = coloniesData.optLong("totalIncome", 0);
            NexusFrame.drawCardHeader(g2, x, y, w, "COLONIES",
                NexusFrame.formatNumber(totalIncome) + "/month");

            int by = y + headerH + bodyPad;

            // Column headers
            g2.setFont(NexusFrame.FONT_SMALL);
            g2.setColor(NexusFrame.TEXT_MUTED);
            g2.drawString("COLONY", x + bodyPad, by + 10);
            g2.drawString("SIZE", x + 220, by + 10);
            g2.drawString("STABILITY", x + 280, by + 10);
            g2.drawString("INCOME", x + w - bodyPad - 60, by + 10);
            by += 20;

            if (colonies != null) {
                g2.setFont(NexusFrame.FONT_MONO);
                for (int i = 0; i < colonies.length(); i++) {
                    JSONObject c = colonies.optJSONObject(i);
                    if (c == null) continue;

                    g2.setColor(NexusFrame.CYAN);
                    g2.drawString(c.optString("name", "?"), x + bodyPad, by + 18);

                    g2.setColor(NexusFrame.TEXT_PRIMARY);
                    g2.drawString(String.valueOf(c.optInt("size", 0)), x + 220, by + 18);

                    double stab = c.optDouble("stability", 0);
                    Color stabColor = stab >= 7 ? NexusFrame.GREEN :
                                      stab >= 4 ? NexusFrame.ORANGE : NexusFrame.RED;
                    g2.setColor(stabColor);
                    g2.drawString(String.format("%.1f", stab), x + 280, by + 18);

                    long income = c.optLong("netIncome", 0);
                    g2.setColor(income >= 0 ? NexusFrame.GREEN : NexusFrame.RED);
                    String incStr = NexusFrame.formatNumber(income);
                    int incW = g2.getFontMetrics().stringWidth(incStr);
                    g2.drawString(incStr, x + w - bodyPad - incW, by + 18);

                    by += rowH;
                }
            }

            return cardH;
        }
    }
}
