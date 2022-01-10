package website.skylorbeck.minecraft.skylorlib.mixin;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(PlayerListEntry.class)
public class CapeMixin {

    @Inject(at = @At("RETURN"),cancellable = true,method = "getCapeTexture")
    public void getSpecialCape(CallbackInfoReturnable<Identifier> cir){
        if (((PlayerListEntry)(Object)this).getProfile().getName().toLowerCase(Locale.ROOT).equals("skylor")){
            cir.setReturnValue(new Identifier("skylorlib","textures/skylorscape.png"));
        }
    }
}
