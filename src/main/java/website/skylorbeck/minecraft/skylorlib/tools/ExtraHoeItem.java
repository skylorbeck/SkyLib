package website.skylorbeck.minecraft.skylorlib.tools;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ToolMaterial;

public class ExtraHoeItem extends HoeItem {//Hoe is protected for some reason
    public ExtraHoeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
}
