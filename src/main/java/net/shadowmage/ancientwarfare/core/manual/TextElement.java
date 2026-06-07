package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class TextElement implements IContentElement {
	private String text;

	public TextElement(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public static TextElement parse(JsonObject elementJson) {
		return new TextElement(GsonHelper.getAsString(elementJson, "text"));
	}
}
