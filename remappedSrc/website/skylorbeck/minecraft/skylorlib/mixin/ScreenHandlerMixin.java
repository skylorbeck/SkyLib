package website.skylorbeck.minecraft.skylorlib.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerMixin {
    @Mutable
    @Accessor("slots")
     void setSlots(DefaultedList<Slot> slots);
    @Mutable
    @Accessor("trackedStacks")
    void setTrackedStacks(DefaultedList<ItemStack> slots);
    @Mutable
    @Accessor("previousTrackedStacks")
    void setPreviousTrackedStacks(DefaultedList<ItemStack> slots);
}
