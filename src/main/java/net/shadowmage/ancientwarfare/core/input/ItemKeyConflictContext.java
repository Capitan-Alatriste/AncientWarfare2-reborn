package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;

public class ItemKeyConflictContext implements IKeyConflictContext {
    public static final ItemKeyConflictContext INSTANCE = new ItemKeyConflictContext();

    private ItemKeyConflictContext() {
    }

    @Override
    public boolean isActive() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.level == null) {
            return false;
        }

        return mc.player.getMainHandItem().getItem() instanceof IItemKeyInterface || mc.player.getOffhandItem().getItem() instanceof IItemKeyInterface;
    }

    @Override
    public boolean conflicts(IKeyConflictContext other) {
        return this == other;
    }
}
