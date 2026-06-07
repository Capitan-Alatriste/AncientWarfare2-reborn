package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;

import net.neoforged.neoforge.common.util.INBTSerializable;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class NBTHelper {
	private NBTHelper() {}

	public static BlockState getBlockState(CompoundTag blockStateTag) {
		return BlockTools.getBlockState(new Tuple<>(blockStateTag.getString("blockName"), getStateProperties(blockStateTag.getCompound("properties"))),
				Block::defaultBlockState, BlockTools::updateProperty);
	}

	private static Map<String, String> getStateProperties(CompoundTag propertiesTag) {
		Map<String, String> ret = new HashMap<>();
		for (String key : propertiesTag.getAllKeys()) {
			if (propertiesTag.contains(key, Tag.TAG_STRING)) {
				ret.put(key, propertiesTag.getString(key));
			}
		}
		return ret;
	}

	public static CompoundTag getBlockStateTag(BlockState state) {
		CompoundTag ret = new CompoundTag();
		//noinspection ConstantConditions
		ret.putString("blockName", net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
		if (!state.getProperties().isEmpty()) {
			ret.put("properties", getStatePropertiesTag(state.getProperties()));
		}
		return ret;
	}

	private static CompoundTag getStatePropertiesTag(ImmutableMap<Property<?>, Comparable<?>> properties) {
		CompoundTag propertiesTag = new CompoundTag();

		for (Map.Entry<Property<?>, Comparable<?>> property : properties.entrySet()) {
			propertiesTag.putString(property.getKey().getName(), serializeValue(property.getKey(), property.getValue()));
		}

		return propertiesTag;
	}

	private static <T extends Comparable<T>> String serializeValue(Property<T> property, Comparable<?> valueString) {
		//noinspection unchecked
		return property.getName((T) valueString);
	}

	public static <T> Set<T> getSet(ListTag tagList, Function<Tag, T> getElement) {
		Set<T> ret = new HashSet<>();
		for (Tag tag : tagList) {
			ret.add(getElement.apply(tag));
		}
		return ret;
	}

	public static <T> List<T> getList(ListTag tagList, Function<Tag, T> getElement) {
		ArrayList<T> ret = new ArrayList<>();
		for (Tag tag : tagList) {
			ret.add(getElement.apply(tag));
		}
		return ret;
	}

	public static Set<String> getStringSet(ListTag tagList) {
		return getSet(tagList, tag -> ((StringTag) tag).getString());
	}

	public static <T> ListTag getTagList(Collection<T> collection, Function<T, Tag> serializeElement) {
		ListTag ret = new ListTag();
		collection.forEach(element -> ret.add(serializeElement.apply(element)));
		return ret;
	}

	public static ListTag getNBTStringList(Collection<String> strings) {
		ListTag ret = new ListTag();
		strings.forEach(str -> ret.add(StringTag.valueOf(str)));
		return ret;
	}

	public static ListTag getNBTUniqueIdList(Collection<UUID> uuids) {
		ListTag ret = new ListTag();
		uuids.forEach(uuid -> ret.add(new NBTBuilder().setUniqueId("uuid", uuid).build()));
		return ret;
	}

	public static Set<UUID> getUniqueIdSet(Tag tag) {
		return getSet(getTagList(tag, Tag.TAG_COMPOUND), element -> ((CompoundTag) element).getUniqueId("uuid"));
	}

	private static ListTag getTagList(Tag tag, int type) {
		try {
			if (tag.getId() == 9) {
				ListTag nbttaglist = (ListTag) tag;

				if (!nbttaglist.isEmpty() && nbttaglist.getElementType() != type) {
					return new ListTag();
				}

				return nbttaglist;
			}
		}
		catch (ClassCastException classcastexception) {
			net.shadowmage.ancientwarfare.core.AncientWarfareCore.LOG.error("Error casting tag to taglist: {}", tag);
		}

		return new ListTag();
	}

	public static final Collector<Tag, ListTag, ListTag> NBTLIST_COLLECTOR =
			Collector.of(ListTag::new, ListTag::add, (l1, l2) -> {
				l2.forEach(l1::add);
				return l1;
			});

	public static CompoundTag writeBlockPosToNBT(CompoundTag tag, BlockPos pos) {
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
		return tag;
	}

	public static BlockPos readBlockPosFromNBT(CompoundTag tag) {
		return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	public static <K, V> Map<K, V> getMap(ListTag list, Function<CompoundTag, K> getKey, Function<CompoundTag, V> getValue) {
		Map<K, V> ret = new HashMap<>();
		for (int i = 0; i < list.tagCount(); i++) {
			CompoundTag tag = list.getCompound(i);

			ret.put(getKey.apply(tag), getValue.apply(tag));
		}

		return ret;
	}

	public static <K, V> ListTag mapToCompoundList(Map<K, V> map, BiConsumer<CompoundTag, K> setKeyTag, BiConsumer<CompoundTag, V> setValueTag) {
		ListTag list = new ListTag();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			CompoundTag nbtEntry = new CompoundTag();
			setKeyTag.accept(nbtEntry, entry.getKey());
			setValueTag.accept(nbtEntry, entry.getValue());

			list.add(nbtEntry);
		}

		return list;
	}

	public static void writeSerializablesTo(CompoundTag tag, String key, List<? extends INBTSerializable> elements) {
		ListTag list = new ListTag();
		for (INBTSerializable serializable : elements) {
			list.add(serializable.serializeNBT());
		}
		tag.put(key, list);
	}

	public static <T extends INBTSerializable<CompoundTag>> List<T> deserializeListFrom(CompoundTag tag, String key, Supplier<T> supplier) {
		ListTag tags = tag.getList(key, Tag.TAG_COMPOUND);
		ArrayList<T> list = new ArrayList<>();
		for (int i = 0; i < tags.tagCount(); i++) {
			T element = supplier.get();
			element.deserializeNBT(tags.getCompound(i));
			list.add(element);
		}
		return list;
	}
}
