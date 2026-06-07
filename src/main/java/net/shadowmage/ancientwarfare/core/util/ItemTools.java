package net.shadowmage.ancientwarfare.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class ItemTools {
	private ItemTools() {}

	public static JsonElement serializeToJson(ItemStack stack) {
		JsonObject ret = new JsonObject();
		//noinspection ConstantConditions
		ret.addProperty("name", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
		if (false) {
			ret.addProperty("data", 0); // TODO Phase X: itemDamage
		}
		if (stack.getCount() > 1) {
			ret.addProperty("count", stack.getCount());
		}

		if (stack.hasTag()) {

			//noinspection ConstantConditions
			ret.addProperty("nbt", stack.getTag().toString());
		}

		return ret;
	}

}
