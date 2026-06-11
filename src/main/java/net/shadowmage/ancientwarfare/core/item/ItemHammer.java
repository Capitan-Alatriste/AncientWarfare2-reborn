package net.shadowmage.ancientwarfare.core.item;

import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemHammer extends ItemBaseCore implements IItemKeyInterface {
	public static final String WORK_MODE_TAG = "workMode";
	private double attackDamage = 5.d;

	private Tier material;

	public ItemHammer(String regName, Tier material, Item.Properties properties) {
		super(properties.stacksTo(1).durability(material.getUses()));
		attackDamage = 4.f + material.getAttackDamageBonus();
		this.material = material;
	}

    public ItemHammer(String regName, Tier material) {
        this(regName, material, new Item.Properties());
    }

	public Tier getMaterial() {
		return material;
	}

	@Override
	public int getEnchantmentValue() {
		return material.getEnchantmentValue();
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (material.getRepairIngredient() != null && material.getRepairIngredient().test(repair))
			return true;
		return super.isValidRepairItem(toRepair, repair);
	}

	@Override
	public boolean hurtEnemy(ItemStack par1ItemStack, LivingEntity par2EntityLivingBase, LivingEntity par3EntityLivingBase) {
		par1ItemStack.hurtAndBreak(1, par3EntityLivingBase, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (state.getDestroySpeed(worldIn, pos) != 0.0D) {
			stack.hurtAndBreak(2, entityLiving, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
		}
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide) {
			return InteractionResultHolder.success(stack);
		}
		boolean workMode = getWorkMode(stack);
		Optional<IWorkSite> site = getWorkSite(world, player);

		if (workMode && site.isPresent() && !player.isShiftKeyDown()) {
			IWorkSite tile = site.get();
			if (tile.hasWork() && !tile.hasWorkers()) {
				int amt = player.isCreative() ? 100 : 1;
				tile.addEnergyFromPlayer(player);
				stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
				// TODO Phase 6: NetworkHandler.INSTANCE.sendToAllAround(new PacketFluidSound(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS), ...);
				return InteractionResultHolder.success(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public InteractionResult useOnFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
		if (context.getLevel().isClientSide) {
			return InteractionResult.PASS;
		}

		boolean workMode = getWorkMode(stack);
		if (!workMode && !context.getPlayer().isShiftKeyDown()) {
            // TODO Phase 5: Rotatable block logic
			// IBlockState state = world.getBlockState(pos);
			// if (state.getBlock() instanceof IRotatableBlock) {
			// 	world.setBlockState(pos, ((IRotatableBlock) state.getBlock()).rotateBlock(world, pos, state, player, side));
			// 	return EnumActionResult.SUCCESS;
			// } else {
			// 	Optional<IRotatableTile> rTe = WorldTools.getTile(world, pos, IRotatableTile.class);
			// 	if (rTe.isPresent()) {
			// 		rTe.get().setPrimaryFacing(rTe.get().getPrimaryFacing().rotateY());
			// 		return EnumActionResult.SUCCESS;
			// 	}
			// }
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("guistrings.core.hammer.description1"));
		tooltip.add(Component.translatable("guistrings.core.hammer.description2"));

		boolean mode = getWorkMode(stack);
		String textMode = mode ? "guistrings.core.hammer.mode.work" : "guistrings.core.hammer.mode.rotate";
		tooltip.add(Component.literal("Mode: ").append(Component.translatable(textMode)));
	}

	@Override
	public void onKeyAction(Player player, ItemStack stack, int key) {
		boolean mode = getWorkMode(stack);
		mode = !mode;
		setWorkMode(stack, mode);

		String textMode = mode ? "guistrings.core.hammer.mode.work" : "guistrings.core.hammer.mode.rotate";
		player.displayClientMessage(Component.translatable(textMode), true);
	}

	private Optional<IWorkSite> getWorkSite(Level world, Player player) {
		// TODO Phase 5: block raytracing
        // RayTraceResult res = WorldTools.rayTraceBlocks(world, player, false);
		// if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
		// 	return WorldTools.getTile(world, res.getBlockPos(), IWorkSite.class);
		// }
		return Optional.empty();
	}

	public static boolean getWorkMode(ItemStack stack) {
		if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			if (tag.contains(WORK_MODE_TAG)) {
				return tag.getBoolean(WORK_MODE_TAG);
			}
		}
		return false;
	}

	public static void setWorkMode(ItemStack stack, boolean workMode) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(WORK_MODE_TAG, workMode));
	}
}
