package gjum.minecraft.forge.gridlines.config;


import gjum.minecraft.forge.gridlines.GridLinesMod;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Set;

public class GridLinesConfig {
    public static final String CATEGORY_MAIN = "Main";

    public static final GridLinesConfig instance = new GridLinesConfig();

    public Configuration config;

    public boolean depthTest;
    public boolean enabled;
    public int renderDistance;

    public boolean grid1Enabled;
    public int grid1Interval;
    public GridPattern grid1GridPattern;
    public int grid1XAnchor, grid1ZAnchor;
    public String grid1Color;

    public boolean grid2Enabled;
    public int grid2Interval;
    public GridPattern grid2GridPattern;
    public int grid2XAnchor, grid2ZAnchor;
    public String grid2Color;

    private Property propDepthTest, propEnabled, propRenderDistance;
    private Property propGrid1Enabled, propGrid1Interval, propGrid1Pattern, propGrid1XAnchor, propGrid1ZAnchor, propGrid1Color;
    private Property propGrid2Enabled, propGrid2Interval, propGrid2Pattern, propGrid2XAnchor, propGrid2ZAnchor, propGrid2Color;

    private GridLinesConfig() {
    }

    public void load(File configFile) {
        config = new Configuration(configFile, GridLinesMod.VERSION);

        syncProperties();
        final ConfigCategory categoryMain = config.getCategory(CATEGORY_MAIN);
        final Set<String> confKeys = categoryMain.keySet();

        config.load();

        if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion())) {
            // clear config from old entries
            // otherwise they would clutter the gui
            final Set<String> unusedConfKeys = categoryMain.keySet();
            unusedConfKeys.removeAll(confKeys);
            for (String confKey : unusedConfKeys) {
                categoryMain.remove(confKey);
            }
        }

        syncProperties();
        syncValues();
    }

    public void afterGuiSave() {
        syncProperties();
        syncValues();
    }

    public void setEnabled(boolean enabled) {
        syncProperties();
        propEnabled.set(enabled);
        syncValues();
    }

    public void setDepthTest(boolean depthTest) {
        syncProperties();
        propDepthTest.set(depthTest);
        syncValues();
    }

    public void setAnchor(int x, int z) {
        syncProperties();
        propGrid1XAnchor.set(x);
        propGrid1ZAnchor.set(z);
        syncValues();
    }

    /**
     * no idea why this has to be called so often, ideally the prop* would stay the same,
     * but it looks like they get disassociated from the config sometimes and setting them no longer has any effect
     */
    private void syncProperties() {
        propDepthTest = config.get(CATEGORY_MAIN, "depth test", true, "hide behind blocks instead of showing through them");
        propEnabled = config.get(CATEGORY_MAIN, "enabled", true, "Enable/disable all overlays");
        propRenderDistance = config.get(CATEGORY_MAIN, "render distance", 32);

        propGrid1Enabled = config.get(CATEGORY_MAIN, "grid1 enabled", true, "Enable/disable Grid1 overlays");
        propGrid1Interval = config.get(CATEGORY_MAIN, "grid1 interval", 4);
        propGrid1Pattern = config.get(CATEGORY_MAIN, "grid1 pattern", GridPattern.SQUARE.name, "what kind of grid", GridPattern.names);
        propGrid1XAnchor = config.get(CATEGORY_MAIN, "grid1 x anchor", 0);
        propGrid1ZAnchor = config.get(CATEGORY_MAIN, "grid1 z anchor", 0);
        propGrid1Color = config.get(CATEGORY_MAIN, "grid1 line color", "#ffffff");

        propGrid2Enabled = config.get(CATEGORY_MAIN, "grid2 enabled", false, "Enable/disable Grid1 overlays");
        propGrid2Interval = config.get(CATEGORY_MAIN, "grid2 interval", 2);
        propGrid2Pattern = config.get(CATEGORY_MAIN, "grid2 pattern", GridPattern.DIAMOND.name, "what kind of grid", GridPattern.names);
        propGrid2XAnchor = config.get(CATEGORY_MAIN, "grid2 x anchor", 2);
        propGrid2ZAnchor = config.get(CATEGORY_MAIN, "grid2 z anchor", 0);
        propGrid2Color = config.get(CATEGORY_MAIN, "grid2 line color", "#888888");
    }

    /**
     * called every time a prop is changed, to apply the new values to the fields and to save the values to the config file
     */
    private void syncValues() {
        depthTest = propDepthTest.getBoolean();
        enabled = propEnabled.getBoolean();
        renderDistance = propRenderDistance.getInt();

        grid1Enabled = propGrid1Enabled.getBoolean();
        grid1Interval = propGrid1Interval.getInt();
        grid1GridPattern = GridPattern.fromName(propGrid1Pattern.getString());
        grid1XAnchor = propGrid1XAnchor.getInt();
        grid1ZAnchor = propGrid1ZAnchor.getInt();
        grid1Color = propGrid1Color.getString();

        grid2Enabled = propGrid2Enabled.getBoolean();
        grid2Interval = propGrid2Interval.getInt();
        grid2GridPattern = GridPattern.fromName(propGrid2Pattern.getString());
        grid2XAnchor = propGrid2XAnchor.getInt();
        grid2ZAnchor = propGrid2ZAnchor.getInt();
        grid2Color = propGrid2Color.getString();

        if (config.hasChanged()) {
            config.save();
            syncProperties();
            GridLinesMod.logger.info("Saved " + GridLinesMod.MOD_NAME + " config.");
        }
    }

}
