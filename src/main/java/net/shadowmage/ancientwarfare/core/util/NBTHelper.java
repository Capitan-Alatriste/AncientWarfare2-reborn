package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.HolderLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class NBTHelper {

    public static CompoundTag writeBlockPos(CompoundTag tag, String name, BlockPos pos) {
        if (pos != null) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            tag.put(name, posTag);
        }
        return tag;
    }

    public static BlockPos readBlockPos(CompoundTag tag, String name) {
        if (tag.contains(name)) {
            CompoundTag posTag = tag.getCompound(name);
            return new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
        }
        return null;
    }

	public static <T extends INBTSerializable<CompoundTag>> ListTag writeList(List<T> list) {
		ListTag tagList = new ListTag();
		for (T element : list) {
			tagList.add(element.serializeNBT(null));
		}
		return tagList;
	}

	public static <T extends INBTSerializable<CompoundTag>> List<T> readList(ListTag tagList, Function<CompoundTag, T> itemCreator) {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag t = tagList.getCompound(i);
			T element = itemCreator.apply(t);
			if (element != null) {
				element.deserializeNBT(null, t);
				list.add(element);
			}
		}
		return list;
	}
}
