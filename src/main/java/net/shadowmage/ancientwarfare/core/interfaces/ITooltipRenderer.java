package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.world.item.ItemStack;


public interface ITooltipRenderer {

	public void handleItemStackTooltipRender(ItemStack itemStack, int mouseX, int mouseY);

	public void handleElementTooltipRender(Object tooltip, int mouseX, int mouseY); // TODO: Phase X Tooltip

}
