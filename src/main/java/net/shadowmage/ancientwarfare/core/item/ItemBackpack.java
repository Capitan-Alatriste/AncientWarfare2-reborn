package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
// TODO Phase 6: import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.minecraft.world.item.Item;
import net.shadowmage.ancientwarfare.core.inventory.ItemHandlerBackpack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBackpack extends ItemBaseCore {

    // Note: In 1.21.1, subitems are typically separate items, or managed via components.
    // For now we assume a basic backpack and leave damage value logic out as it is mostly removed in modern versions.
	public ItemBackpack(Item.Properties properties) {
		super(properties.stacksTo(1));
	}

    public ItemBackpack() {
        super(new Item.Properties().stacksTo(1));
    }

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        // Fallback size display, originally dependent on metadata/damage
		tooltip.add(Component.translatable("guistrings.core.backpack.size", 9));
		tooltip.add(Component.translatable("guistrings.core.backpack.click_to_open"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide) {
			// TODO Phase 6: NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BACKPACK, 0, 0, 0);
        }
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

    // TODO Phase 9 (capabilities): 1.21.1 NeoForge uses capabilities differently, typically registered in RegisterCapabilitiesEvent.
    // The ItemHandlerBackpack capability logic should be migrated there.
}
