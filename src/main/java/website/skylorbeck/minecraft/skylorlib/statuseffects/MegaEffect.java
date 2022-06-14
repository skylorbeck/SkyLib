package website.skylorbeck.minecraft.skylorlib.statuseffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import website.skylorbeck.minecraft.skylorlib.IMegable;


public class MegaEffect extends StatusEffect {
    public MegaEffect() {
        super(StatusEffectCategory.BENEFICIAL, 16773073);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return duration <=10;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof IMegable megable){
            megable.Megafy();
        }
        super.applyUpdateEffect(entity, amplifier);
    }
}
