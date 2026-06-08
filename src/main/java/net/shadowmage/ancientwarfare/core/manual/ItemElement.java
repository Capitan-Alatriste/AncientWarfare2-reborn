package net.shadowmage.ancientwarfare.core.manual;



import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;



import java.util.ArrayList;
import java.util.List;

public class ItemElement implements IContentElement {
	private ItemStack[] itemStack;

	private ItemElement(ItemStack... itemStack) {
		this.itemStack = itemStack;
	}

	public ItemStack[] getItemStacks() {
		return itemStack;
	}

	public static IContentElement parse(JsonObject elementJson) {
		if (elementJson.has("items")) {
			List<ItemStack> stacks = new ArrayList<>();
			elementJson.getAsJsonArray("items").forEach(e -> stacks.add(ItemStack.EMPTY /* TODO Phase 4: JsonHelper.getItemStack(e) */));
			return new ItemElement(stacks.toArray(new ItemStack[stacks.size()]));
		}
		return new ItemElement(ItemStack.EMPTY /* TODO Phase 4: JsonHelper.getItemStack(elementJson) */);
	}
}
