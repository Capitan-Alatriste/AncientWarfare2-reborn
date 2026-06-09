package net.shadowmage.ancientwarfare.core.inventory;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.IIngredientCount;
import net.shadowmage.ancientwarfare.core.tile.CraftingRecipeMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * This needs to be used instead of vanilla SlotCrafting because vanilla one can only work with ingredients of Recipes, but has no clue what to do
 * with other recipe ingredients and thus returns everything in CraftingMatrix in the getRemainingItems call and thus there's item dupe in that case.
 */
public class SlotResearchCrafting extends Slot {
	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final CraftingContainer craftMatrix;
	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private final Player player;
	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int amountCrafted;

	private CraftingRecipeMemory craftingRecipeMemory;

	public SlotResearchCrafting(Player player, CraftingRecipeMemory craftingRecipeMemory, CraftingContainer craftingInventory, Container inventory, int slotIndex, int xPosition, int yPosition) {
		super(inventory, slotIndex, xPosition, yPosition);
		this.player = player;
		this.craftMatrix = craftingInventory;
		this.craftingRecipeMemory = craftingRecipeMemory;
	}

	/**
	 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
	 */
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	public ItemStack decrStackSize(int amount) {
		if (hasItem()) {
			amountCrafted += Math.min(amount, getItem().getCount());
		}
		return super.remove(amount);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	protected void onCrafting(ItemStack stack, int amount) {
		amountCrafted += amount;
		onCrafting(stack);
	}

	protected void onSwapCraft(int p_190900_1_) {
		amountCrafted += p_190900_1_;
	}

	@Override
	protected void onCrafting(ItemStack stack) {
		ResultContainer inventorycraftresult = (ResultContainer) inventory;
		Recipe irecipe = inventorycraftresult.getRecipeUsed();
		if (amountCrafted > 0) {
			stack.onCraftedBy(player.level(), player, amountCrafted);
			if (irecipe != null) {
				net.neoforged.neoforge.event.EventHooks.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
			}
		}
		amountCrafted = 0;
		if (irecipe != null && !irecipe.isDynamic()) {
			player.unlockRecipes(Lists.newArrayList(irecipe));
			inventorycraftresult.setRecipeUsed(null);
		}
	}

	@Override
	public ItemStack onTake(Player thePlayer, ItemStack stack) {
		onCrafting(stack);

		ICraftingRecipe recipe = craftingRecipeMemory.getRecipe();

		net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(thePlayer);
		NonNullList<ItemStack> nonnulllist = recipe.getRemainingItems(craftMatrix);
		net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(null);

		List<Integer> usedIngredients = new ArrayList<>();
		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = this.craftMatrix.getItem(i);
			ItemStack itemstack1 = nonnulllist.get(i);
			if (!itemstack.isEmpty()) {
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				ItemStack finalStack = itemstack;
				//TODO this potentially has an issue if two ingredients use the same item just with different counts.
				// May match the lower count one first and then not have another ingredient that would mach the higher count one.
				// Would need more logic to order ingredients by what amount they're able to match and always try to use lowest one first.
				Ingredient ingredient = ingredients.stream().filter(in -> !usedIngredients.contains(ingredients.indexOf(in)) && in.apply(finalStack)).findFirst().orElse(Ingredient.EMPTY);
				usedIngredients.add(ingredients.indexOf(ingredient));
				craftMatrix.decrStackSize(i, ingredient instanceof IIngredientCount ? ((IIngredientCount) ingredient).getCount() : 1);
				itemstack = this.craftMatrix.getItem(i);
			}
			if (!itemstack1.isEmpty()) {
				if (itemstack.isEmpty()) {
					this.craftMatrix.setItem(i, itemstack1);
				} else if (ItemStack.isSameItem(itemstack, itemstack1) && ItemStack.isSameItemSameComponents(itemstack, itemstack1)) {
					itemstack1.grow(itemstack.getCount());
					this.craftMatrix.setItem(i, itemstack1);
				} else if (!this.player.getInventory().add(itemstack1)) {
					this.player.dropItem(itemstack1, false);
				}
			}
		}
		return stack;
	}
}
