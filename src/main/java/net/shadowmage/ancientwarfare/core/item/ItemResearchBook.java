package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
// TODO Phase 7: import net.shadowmage.ancientwarfare.core.gui.GuiResearchBook;
// TODO Phase 9: import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
// TODO Phase 6: import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemResearchBook extends ItemBaseCore {

	public ItemResearchBook(Item.Properties properties) {
		super(properties.stacksTo(1));
	}

    public ItemResearchBook() {
        this(new Item.Properties());
    }

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		String name = getResearcherName(stack);
		if (name == null) {
			tooltip.add(Component.translatable("guistrings.research.researcher_name").append(": ").append(Component.translatable("guistrings.research.no_researcher")));
			tooltip.add(Component.translatable("guistrings.research.right_click_to_bind"));
		} else {
			tooltip.add(Component.translatable("guistrings.research.researcher_name").append(": ").append(name));
			tooltip.add(Component.translatable("guistrings.research.right_click_to_view"));
		}
	}

	@Nullable
	public static String getResearcherName(ItemStack stack) {
		if (!stack.isEmpty() /* TODO Phase 9: && stack.getItem() == AWCoreItems.RESEARCH_BOOK */ && stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (tag.contains("researcherName")) {
			    return tag.getString("researcherName");
            }
		}
		return null;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			if (getResearcherName(stack) == null) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString("researcherName", player.getName().getString()));
				player.displayClientMessage(Component.translatable("guistrings.research.book_bound"), false);
			} else {
				// TODO Phase 6: NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_BOOK, 0, 0, 0);
			}
		}
		return InteractionResultHolder.success(stack);
	}

	// @Override
	// public void registerClient() {
	// 	super.registerClient();
	// 	NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_BOOK, GuiResearchBook.class);
	// }
}
