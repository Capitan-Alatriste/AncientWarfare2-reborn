package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class ImageElement implements IContentElement {
	private String path;
	private final int width;
	private final int height;

	private ImageElement(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;
	}

	public static ImageElement parse(JsonObject elementJson) {
		return new ImageElement(GsonHelper.getAsString(elementJson, "path"), GsonHelper.getAsInt(elementJson, "width"),
				GsonHelper.getAsInt(elementJson, "height"));
	}

	public String getPath() {
		return path;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
