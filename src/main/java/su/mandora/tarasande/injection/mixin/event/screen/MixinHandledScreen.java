package su.mandora.tarasande.injection.mixin.event.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    protected Slot focusedSlot;

    @Shadow protected int x;
    @Shadow protected int y;

    @Shadow @org.jetbrains.annotations.Nullable protected abstract Slot getSlotAt(double xPosition, double yPosition);

    @Shadow public abstract T getScreenHandler();

    @Shadow private boolean doubleClicking;

    @Shadow protected abstract void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType);

    private static final ItemStack[] ITEMS = new ItemStack[27];

    public MixinHandledScreen(Text title) {
        super(title);
    }

    // Inventory Tweaks
    @Inject(method = "mouseDragged", at = @At("TAIL"))
    private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> info) {
        if (button != GLFW_MOUSE_BUTTON_LEFT || doubleClicking) return;

        Slot slot = getSlotAt(mouseX, mouseY);
        if (slot != null && slot.hasStack() && hasShiftDown()) onMouseClick(slot, slot.id, button, SlotActionType.QUICK_MOVE);
    }
}