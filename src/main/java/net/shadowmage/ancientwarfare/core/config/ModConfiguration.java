package net.shadowmage.ancientwarfare.core.config;

public abstract class ModConfiguration {
	public static final String generalOptions = "01_shared_settings";
	public static final String serverOptions = "02_server_settings";
	public static final String clientOptions = "03_client_settings";
	public static final String configPathForFiles = "config/ancientwarfare/";
	public boolean updatedVersion = false;
	public boolean autoExportOnUpdate = false;

	public ModConfiguration() {
	}

	public ModConfiguration(String modid) {
	}

	protected abstract void initializeCategories();

	protected abstract void initializeValues();

	public boolean updatedVersion() {
		return updatedVersion;
	}

	public boolean autoExportOnUpdate() {
		return autoExportOnUpdate;
	}
}
