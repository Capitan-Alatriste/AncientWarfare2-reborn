package net.shadowmage.ancientwarfare.core.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface.ItemAltFunction;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketItemMouseScroll;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class InputHandler {

    private static final String CATEGORY = "keybind.category.awCore";
    public static final KeyMapping ALT_ITEM_USE_1 = new KeyMapping(AWCoreStatics.KEY_ALT_ITEM_USE_1, ItemKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping ALT_ITEM_USE_2 = new KeyMapping(AWCoreStatics.KEY_ALT_ITEM_USE_2, ItemKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    public static final KeyMapping ALT_ITEM_USE_3 = new KeyMapping(AWCoreStatics.KEY_ALT_ITEM_USE_3, ItemKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping ALT_ITEM_USE_4 = new KeyMapping(AWCoreStatics.KEY_ALT_ITEM_USE_4, ItemKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping ALT_ITEM_USE_5 = new KeyMapping(AWCoreStatics.KEY_ALT_ITEM_USE_5, ItemKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);

    private static final Set<InputCallbackDispatcher> keybindingCallbacks = new HashSet<>();

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ALT_ITEM_USE_1);
        event.register(ALT_ITEM_USE_2);
        event.register(ALT_ITEM_USE_3);
        event.register(ALT_ITEM_USE_4);
        event.register(ALT_ITEM_USE_5);

        initCallbacks();
    }

    private static void initCallbacks() {
        registerCallBack(ALT_ITEM_USE_1, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_1));
        registerCallBack(ALT_ITEM_USE_2, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_2));
        registerCallBack(ALT_ITEM_USE_3, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_3));
        registerCallBack(ALT_ITEM_USE_4, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_4));
        registerCallBack(ALT_ITEM_USE_5, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_5));
    }

    public static void registerCallBack(KeyMapping keyBinding, IInputCallback callback) {
        Predicate<InputCallbackDispatcher> matchingKeyBinding = d -> d.getKeyBinding().equals(keyBinding);
        if (keybindingCallbacks.stream().anyMatch(matchingKeyBinding)) {
            keybindingCallbacks.stream().filter(matchingKeyBinding).findFirst().ifPresent(d -> d.addInputCallback(callback));
        } else {
            keybindingCallbacks.add(new InputCallbackDispatcher(keyBinding, callback));
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        if (event.getAction() == GLFW.GLFW_PRESS) {
            keybindingCallbacks.stream().filter(k -> k.getKeyBinding().consumeClick()).forEach(InputCallbackDispatcher::onKeyPressed);
        }
    }

    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || !player.isCrouching() || event.getScrollDeltaY() == 0) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (item instanceof IScrollableItem scrollableItem) {
            if (event.getScrollDeltaY() > 0) {
                if (scrollableItem.onScrollUp(player.level(), player, stack)) {
                    NetworkHandler.sendToServer(new PacketItemMouseScroll(true));
                }
            } else {
                if (scrollableItem.onScrollDown(player.level(), player, stack)) {
                    NetworkHandler.sendToServer(new PacketItemMouseScroll(false));
                }
            }
            event.setCanceled(true);
        }
    }
}
