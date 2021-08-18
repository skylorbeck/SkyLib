package website.skylorbeck.minecraft.skylorlib.tools;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class ExtraPickaxeItem extends PickaxeItem {//Pickaxe is protected for some reason
    public ExtraPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
}
