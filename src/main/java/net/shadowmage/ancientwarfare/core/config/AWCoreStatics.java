package net.shadowmage.ancientwarfare.core.config;



public class AWCoreStatics extends ModConfiguration {

	public static final String KEY_ALT_ITEM_USE_1 = "keybind.awCore.alt.item.use.1";
	public static final String KEY_ALT_ITEM_USE_2 = "keybind.awCore.alt.item.use.2";
	public static final String KEY_ALT_ITEM_USE_3 = "keybind.awCore.alt.item.use.3";
	public static final String KEY_ALT_ITEM_USE_4 = "keybind.awCore.alt.item.use.4";
	public static final String KEY_ALT_ITEM_USE_5 = "keybind.awCore.alt.item.use.5";

	public static boolean DEBUG = false;
	public static final String resourcePath = "/assets/ancientwarfare/resources/";
	public static final String utilsExportPath = AWCoreStatics.configPathForFiles + "/export/";

	/*
	 * research options
	 */
	public static boolean useResearchSystem = true;
	public static boolean enableResearchResourceUse = true;
	public static double energyPerResearchUnit = 1D;
	public static double researchPerTick = 1;

	/*
	 * server options
	 */
	public static boolean fireBlockBreakEvents = true;
	public static boolean includeResearchInChests = true;
	public static double energyPerWorkUnit = 50D;

	public AWCoreStatics(String modid) {
		super(modid);
	}

	@Override
	public void initializeValues() {
        // Deferred ModConfigSpec binding to Phase 9
	}

	@Override
	public void initializeCategories() {
        // Deferred ModConfigSpec binding to Phase 9
	}

	public static boolean isItemCraftable(Object item) {
		return true;
	}

	public static boolean isItemResearcheable(Object item) {
		return true;
	}

	public static void update() {
	}

}
