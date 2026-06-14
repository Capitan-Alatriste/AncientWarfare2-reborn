package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IItemKeyInterface {

    enum ItemAltFunction {
        ALT_FUNCTION_1,
        ALT_FUNCTION_2,
        ALT_FUNCTION_3,
        ALT_FUNCTION_4,
        ALT_FUNCTION_5
    }

    boolean onKeyActionClient(Player player, ItemStack stack, ItemAltFunction altFunction);

    void onKeyAction(Player player, ItemStack stack, ItemAltFunction altFunction);
}
