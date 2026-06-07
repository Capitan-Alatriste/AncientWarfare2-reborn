package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class TableOfContentsElement implements IContentElement {
	private final List<TableOfContentsItem> items;

	public TableOfContentsElement(List<TableOfContentsItem> contentItems) {
		items = contentItems;
	}

	public static TableOfContentsElement parse(JsonObject elementJson) {
		JsonArray contents = GsonHelper.getAsJsonArray(elementJson, "items");

		ArrayList<TableOfContentsItem> tocItems = new ArrayList<>();
		for (JsonElement e : contents) {
			JsonObject contentItem = e.getAsJsonObject();
			tocItems.add(new TableOfContentsItem(GsonHelper.getAsString(contentItem, "text"), GsonHelper.getAsString(contentItem, "category_link")));
		}

		return new TableOfContentsElement(tocItems);
	}

	public List<TableOfContentsItem> getItems() {
		return items;
	}

	public static class TableOfContentsItem {
		private final String text;
		private final String category;

		private TableOfContentsItem(String text, String category) {
			this.text = text;
			this.category = category;
		}

		public String getText() {
			return text;
		}

		public String getCategory() {
			return category;
		}
	}
}
