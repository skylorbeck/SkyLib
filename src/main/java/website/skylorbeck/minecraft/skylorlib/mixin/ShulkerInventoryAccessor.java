package website.skylorbeck.minecraft.skylorlib.mixin;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerBoxBlockEntity.class)
public interface ShulkerInventoryAccessor {
    @Accessor("inventory")
    DefaultedList<ItemStack> getInventory();
    @Accessor("inventory")
    void setInventory(DefaultedList<ItemStack> inventory);
}
