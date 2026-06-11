package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ItemResearchNotes extends ItemBaseCore {

	private static final String RESEARCH_NAME_TAG = "researchName";
	private NonNullList<ItemStack> displayCache = null;

	public ItemResearchNotes(Item.Properties properties) {
		super(properties);
	}

    public ItemResearchNotes() {
        this(new Item.Properties());
    }

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		String researchName = "corrupt_item";
		boolean known = false;
		if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (tag.contains(RESEARCH_NAME_TAG)) {
			    String name = tag.getString(RESEARCH_NAME_TAG);
			    if (ResearchRegistry.researchExists(name) && Minecraft.getInstance().player != null) {
				researchName = Component.translatable(ResearchGoal.getUnlocalizedName(name)).getString();
				known = ResearchTracker.instance().hasPlayerCompleted(Minecraft.getInstance().player.getName().getString(), name);
			    } else {
				researchName = "missing_goal_for_id_" + name;
			    }
            }
		}
		tooltip.add(Component.literal(researchName));
		if (known) {
			tooltip.add(Component.translatable("guistrings.research.known_research"));
			tooltip.add(Component.translatable("guistrings.research.click_to_add_progress"));
		} else {
			tooltip.add(Component.translatable("guistrings.research.unknown_research"));
			tooltip.add(Component.translatable("guistrings.research.click_to_learn"));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide && stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			if (tag.contains(RESEARCH_NAME_TAG)) {
				String name = tag.getString(RESEARCH_NAME_TAG);
				if (ResearchRegistry.researchExists(name)) {
					boolean known = ResearchTracker.instance().hasPlayerCompleted(player.getName().getString(), name);
					if (!known) {
						if (ResearchTracker.instance().addResearchFromNotes(world, player.getName().getString(), name)) {
							player.displayClientMessage(Component.translatable("guistrings.research.learned_from_item", Component.translatable(name).getString()), false);
							stack.shrink(1);
						}
					} else {
						if (ResearchTracker.instance().addProgressFromNotes(world, player.getName().getString(), name)) {
							player.displayClientMessage(Component.translatable("guistrings.research.added_progress", Component.translatable(name).getString()), false);
							stack.shrink(1);
						}
					}
				}
			}
		}
		return InteractionResultHolder.success(stack);
	}
}
