package website.skylorbeck.minecraft.skylorlib.mixin;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BarrelBlockEntity.class)
public interface BarrelInventoryAccessor {
    @Accessor("inventory")
    DefaultedList<ItemStack> getInventory();
    @Accessor("inventory")
    void setInventory(DefaultedList<ItemStack> inventory);
}
