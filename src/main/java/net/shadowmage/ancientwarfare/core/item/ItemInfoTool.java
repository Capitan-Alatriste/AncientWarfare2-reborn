package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemInfoTool extends ItemBaseCore {

    private static final String MODE_TAG = "info_tool_mode";

	public ItemInfoTool(Item.Properties properties) {
		super(properties.stacksTo(1));
	}

    public ItemInfoTool() {
        this(new Item.Properties().stacksTo(1));
    }

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.literal(getMode(stack).name() + " mode"));
	}

	private void printSimpleMessage(Player player, BlockState state) {
		player.displayClientMessage(Component.literal("Block name: " + BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString()), false);
		if (!state.getValues().isEmpty()) {
			player.displayClientMessage(Component.literal("Properties:"), false);
			for (Map.Entry<Property<?>, Comparable<?>> prop : state.getValues().entrySet()) {
				player.displayClientMessage(Component.literal(prop.getKey().getName() + " : " + prop.getValue().toString()), false);
			}
		}
	}

	private void printJSON(Player player, BlockState state) {
        // TODO Phase 2: String json = BlockTools.serializeToJson(state).toString();
        // player.displayClientMessage(Component.literal("Block JSON: " + json), false);
        player.displayClientMessage(Component.literal("Block JSON functionality requires Phase 2 BlockTools port."), false);
	}

	public void printItemInfo(Player player, ItemStack infoTool, ItemStack stack) {
		Mode mode = getMode(infoTool);
		switch (mode) {
			case INFO:
				printSimpleMessage(player, stack);
				break;
			case JSON:
				printJSON(player, stack);
				break;
			case LOOT_ENTRY:
				printLootEntryJSON(player, stack);
				break;
		}
	}

	private void printSimpleMessage(Player player, ItemStack stack) {
		player.displayClientMessage(Component.literal("Item name: " + BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()), false);
		if (stack.has(DataComponents.CUSTOM_DATA)) {
			player.displayClientMessage(Component.literal("NBT: " + stack.get(DataComponents.CUSTOM_DATA).copyTag().toString()), false);
		}
	}

	private void printJSON(Player player, ItemStack stack) {
        // TODO Phase 2: ItemTools serialization
        player.displayClientMessage(Component.literal("Item JSON functionality requires Phase 2 porting."), false);
	}

	private void printLootEntryJSON(Player player, ItemStack stack) {
        player.displayClientMessage(Component.literal("LootEntry functionality needs modern Loot API porting."), false);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide) {
			// TODO Phase 6/7: NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_INFO_TOOL, 0, 0, 0);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResult useOnFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
		if (context.getLevel().isClientSide)
			return InteractionResult.PASS;

		Mode mode = getMode(stack);
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		switch (mode) {
			case INFO:
				printSimpleMessage(context.getPlayer(), state);
				break;
			case JSON:
				printJSON(context.getPlayer(), state);
				break;
			case LOOT_ENTRY:
				break;
		}
		return InteractionResult.SUCCESS;
	}

	public static Mode getMode(ItemStack stack) {
		if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			if (tag.contains(MODE_TAG)) {
				return Mode.values()[tag.getInt(MODE_TAG)];
			}
		}
		return Mode.INFO;
	}

	public static void setMode(ItemStack stack, Mode mode) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(MODE_TAG, mode.ordinal()));
	}

	public enum Mode {
		INFO("Info"), JSON("JSON"), LOOT_ENTRY("Loot Entry");

		private final String displayName;

		Mode(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}
