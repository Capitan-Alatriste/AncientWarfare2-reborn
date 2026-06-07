package net.shadowmage.ancientwarfare.core.util.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.TagParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;

import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.RegistryTools;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class JsonHelper {

	public static BlockState getBlockState(JsonObject parent, String elementName) {
		return getBlockState(parent, elementName, Block::getDefaultState, BlockTools::updateProperty);
	}

	public static BlockState getBlockState(JsonElement json) {
		return BlockTools.getBlockState(getBlockNameAndProperties(json), Block::getDefaultState, BlockTools::updateProperty);
	}

	public static BlockStateMatcher getBlockStateMatcher(JsonElement stateJson) {
		return BlockTools.getBlockState(getBlockNameAndProperties(stateJson), BlockStateMatcher::new, BlockStateMatcher::addProperty);
	}

	public static BlockStateMatcher getBlockStateMatcher(JsonObject stateJson) {
		return getBlockState(stateJson, BlockStateMatcher::new, BlockStateMatcher::addProperty);
	}

	public static BlockStateMatcher getBlockStateMatcher(JsonObject parent, String elementName) {
		return getBlockState(parent, elementName, BlockStateMatcher::new, BlockStateMatcher::addProperty);
	}

	public static ItemStack getItemStack(JsonElement json) {
		return getItemStack(json, (i, c, m, t, n) -> {
			ItemStack stack = new ItemStack(i, c, m);
			stack.setTagCompound(t);
			return stack;
		});
	}

	public static ItemStack getItemStack(JsonObject json, String elementName) {
		if (!GsonHelper.isValidNode(json, elementName)) {
			throw new JsonParseException(getMissingMemberErrorMessage(json, elementName));
		}

		return getItemStack(json.get(elementName));
	}

	private static String getMissingMemberErrorMessage(JsonObject json, String elementName) {
		return "Expected " + elementName + " member in " + json.toString();
	}

	private static <T> T getItemStack(JsonElement element, ItemStackCreator<T> creator) {
		if (element.isJsonPrimitive()) {
			return creator.instantiate(RegistryTools.getItem(element.getAsString()), 1, -1, null, false);
		}

		JsonObject obj = element.getAsJsonObject();
		String registryName = GsonHelper.getAsString(obj, "name");
		Item item = RegistryTools.getItem(registryName);

		int count = GsonHelper.isValidNode(obj, "count") ? GsonHelper.getAsInt(obj, "count") : 1;

		int meta = -1;
		if (GsonHelper.isValidNode(obj, "data")) {
			meta = GsonHelper.getAsInt(obj, "data");
		}
		CompoundTag tagCompound = null;
		if (GsonHelper.isValidNode(obj, "nbt")) {
			try {
				tagCompound = TagParser.parseTag(GsonHelper.getAsString(obj, "nbt"));
			}
			catch (CommandSyntaxException e) {
				net.shadowmage.ancientwarfare.core.AncientWarfareCore.LOG.error("Error reading item stack nbt {}", GsonHelper.getAsJsonObject(obj, "nbt"));
			}
		}

		boolean ignoreNbt = GsonHelper.isValidNode(obj,"ignore_nbt") && GsonHelper.getAsBoolean(obj, "ignore_nbt");

		return creator.instantiate(item, count, meta, tagCompound, ignoreNbt);
	}

	public static ItemStackMatcher getItemStackMatcher(JsonElement element) {
		return getItemStack(element, (i, c, m, t, n) -> new ItemStackMatcher.Builder(i).setMeta(m).setTagCompound(t).setIgnoreNbt(n).build());
	}

	public static ItemStackMatcher getItemStackMatcher(JsonObject parent, String elementName) {
		if (!GsonHelper.isValidNode(parent, elementName)) {
			throw new JsonParseException(getMissingMemberErrorMessage(parent, elementName));
		}

		return getItemStackMatcher(parent.get(elementName));
	}

	private static <T> T getBlockState(JsonObject stateJson, Function<Block, T> init, BlockTools.AddPropertyFunction<T> addProperty) {
		return BlockTools.getBlockState(getBlockNameAndProperties(stateJson), init, addProperty);
	}

	private static <T> T getBlockState(JsonObject parent, String elementName, Function<Block, T> init, BlockTools.AddPropertyFunction<T> addProperty) {
		return BlockTools.getBlockState(getBlockNameAndProperties(parent, elementName), init, addProperty);
	}

	private static Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonObject stateJson) {
		Map<String, String> properties = new HashMap<>();

		if (GsonHelper.isValidNode(stateJson, "properties")) {
			GsonHelper.getAsJsonObject(stateJson, "properties").entrySet().forEach(p -> properties.put(p.getKey(), p.getValue().getAsString()));
		}

		return new Tuple<>(GsonHelper.getAsString(stateJson, "name"), properties);
	}

	private static Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonElement stateElement) {
		if (stateElement.isJsonPrimitive()) {
			return new Tuple<>(GsonHelper.getAsString(stateElement, ""), new HashMap<>());
		}

		return getBlockNameAndProperties(GsonHelper.getAsJsonObject(stateElement, ""));
	}

	private static Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonObject parent, String elementName) {
		if (!GsonHelper.isValidNode(parent, elementName)) {
			throw new JsonParseException(getMissingMemberErrorMessage(parent, elementName));
		}
		return getBlockNameAndProperties(parent.get(elementName));
	}

	public static Predicate<BlockState> getBlockStateMatcher(JsonObject json, String arrayElement, String individualElement) {
		if (json.has(arrayElement)) {
			JsonArray stateMatchers = GsonHelper.getAsJsonArray(json, arrayElement);
			return new MultiBlockStateMatcher(StreamSupport.stream(stateMatchers.spliterator(), false)
					.map(e -> getBlockStateMatcher(GsonHelper.getAsJsonObject(e, individualElement)))
					.toArray(BlockStateMatcher[]::new));
		}
		return getBlockStateMatcher(json, individualElement);
	}

	public static PropertyState getPropertyState(BlockState state, JsonObject parent, String elementName) {
		JsonObject jsonProperty = GsonHelper.getAsJsonObject(parent, elementName);

		if (jsonProperty.entrySet().isEmpty()) {
			throw new JsonParseException("Expected at least one property defined for " + elementName + " in " + parent.toString());
		}

		Entry<String, JsonElement> propJson = jsonProperty.entrySet().iterator().next();
		String propName = propJson.getKey();
		String propValue = propJson.getValue().getAsString();

		return BlockTools.getPropertyState(state.getBlock(), state.getBlock().getBlockState(), propName, propValue);
	}

	public static PropertyStateMatcher getPropertyStateMatcher(BlockState state, JsonObject parent, String elementName) {
		return new PropertyStateMatcher(getPropertyState(state, parent, elementName));
	}

	public static <K, V> Map<K, V> mapFromJson(JsonObject json, String propertyName, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		return mapFromObjectProperties(GsonHelper.getAsJsonObject(json, propertyName), new HashMap<>(), parseKey, parseValue);
	}

	public static <K, V> Map<K, V> mapFromJson(JsonElement json, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		return mapFromJson(GsonHelper.getAsJsonObject(json, ""), parseKey, parseValue);
	}

	public static <K, V> Map<K, V> mapFromJson(JsonObject json, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		return mapFromObjectProperties(json, new HashMap<>(), parseKey, parseValue);
	}

	public static <K, V> void mapFromJson(JsonObject json, String propertyName, Map<K, V> ret, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		mapFromObjectProperties(GsonHelper.getAsJsonObject(json, propertyName), ret, parseKey, parseValue);
	}

	public static <K, V> Map<K, V> mapFromObjectArray(JsonArray jsonArray, String keyName, String valueName, Function<JsonElement, K> parseKey,
			Function<JsonElement, V> parseValue) {
		Map<K, V> ret = new HashMap<>();
		for (JsonElement element : jsonArray) {
			JsonObject entry = GsonHelper.getAsJsonObject(element, "");
			ret.put(parseKey.apply(entry.get(keyName)), parseValue.apply(entry.get(valueName)));
		}
		return ret;
	}

	private static <K, V> Map<K, V> mapFromObjectProperties(JsonObject jsonObject, Map<K, V> ret, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {

		for (Map.Entry<String, JsonElement> pair : jsonObject.entrySet()) {
			ret.put(parseKey.apply(pair), parseValue.apply(pair));
		}

		return ret;
	}

	public static List<ItemStack> getItemStacks(JsonArray stacks) {
		List<ItemStack> ret = NonNullList.create();
		for (JsonElement stackElement : stacks) {
			ret.add(getItemStack(GsonHelper.getAsJsonObject(stackElement, "itemstack")));
		}
		return ret;
	}

	public static <V> Set<V> setFromJson(JsonElement element, Function<JsonElement, V> getElement) {
		return setFromJson(GsonHelper.getAsJsonArray(element, ""), getElement);
	}

	private static <V> Set<V> setFromJson(JsonArray array, Function<JsonElement, V> getElement) {
		Set<V> ret = new HashSet<>();

		for (JsonElement element : array) {
			ret.add(getElement.apply(element));
		}

		return ret;
	}

	@SuppressWarnings({"squid:S4042", "squid:S899"})
	public static void saveJsonFile(JsonObject parent, File file) {
		if (file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}
		try {
			if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
				net.shadowmage.ancientwarfare.core.AncientWarfareCore.LOG.error("Unable to create folders for file : {}", file.getAbsolutePath());
			}
			if (!file.createNewFile()) {
				net.shadowmage.ancientwarfare.core.AncientWarfareCore.LOG.error("Unable to create new file : {}", file.getAbsolutePath());
			}
		}
		catch (IOException e) {
			net.shadowmage.ancientwarfare.core.AncientWarfareCore.LOG.error("Error creating file", e);
		}
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(),
				StandardOpenOption.WRITE, StandardOpenOption.CREATE);
				JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("    ");
			Streams.write(parent, jsonWriter);
		}
		catch (IOException e) {
			net.shadowmage.ancientwarfare.core.AncientWarfareCore.LOG.error("Error saving Json file", e);
		}
	}

	private interface ItemStackCreator<R> {
		R instantiate(Item item, int count, int meta, @Nullable CompoundTag tagCompound, boolean ignoreNbt);
	}
}
