package website.skylorbeck.minecraft.skylorlib.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import website.skylorbeck.minecraft.skylorlib.furnaces.AbstractExtraFurnaceBlockEntity;

@Mixin(FurnaceOutputSlot.class)
public class XPFixMixin extends Slot {//this fix is to make custom furnaces give xp on crafts
    @Shadow
    @Final
    private PlayerEntity player;
    public XPFixMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;onCraft(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
    private void craft(ItemStack stack, CallbackInfo ci) {
        if (!this.player.world.isClient && this.inventory instanceof AbstractExtraFurnaceBlockEntity) {//make sure it's the server and it's a custom furnace block
            ((AbstractExtraFurnaceBlockEntity) this.inventory).dropExperience(this.player);
        }
    }
}
