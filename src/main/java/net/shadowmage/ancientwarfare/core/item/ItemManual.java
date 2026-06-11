package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
// TODO Phase 7: import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
// TODO Phase 9: import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
// TODO Phase 6: import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemManual extends ItemBaseCore {
	public ItemManual(Item.Properties properties) {
		super(properties.stacksTo(1));
		NeoForge.EVENT_BUS.register(this);
	}

    public ItemManual() {
        this(new Item.Properties());
    }

	// @Override
	// public void registerClient() {
	// 	super.registerClient();
	// 	NetworkHandler.registerGui(NetworkHandler.GUI_MANUAL, GuiManual.class);
	// }

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		// TODO Phase 6: NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_MANUAL);
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("item.manual.tooltip"));
	}

	@SubscribeEvent
	public void handlePlayerFirstAWCraft(PlayerEvent.ItemCraftedEvent evt) {
		Player player = evt.getEntity();

		if (player.level().isClientSide) {
			return;
		}

		Item item = evt.getCrafting().getItem();
        // TODO Phase 9:
		// if (item != AWCoreItems.MANUAL && net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).getNamespace().startsWith("ancientwarfare")) {
		// 	WorldData data = AWGameData.INSTANCE.getPerWorldData(player.level(), WorldData.class);
		// 	if (!data.wasPlayerGivenManual(player)) {
		// 		ItemEntity manualDrop = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), new ItemStack(AWCoreItems.MANUAL));
		// 		manualDrop.setPickUpDelay(0);
		// 		player.level().addFreshEntity(manualDrop);
		// 		data.addPlayerThatWasGivenManual(player);
		// 	}
		// }
	}
}
