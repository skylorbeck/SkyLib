package website.skylorbeck.minecraft.skylorlib;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Registrar {
    public static void regItem(String name, Item itemid,String MODID){
        Registry.register(Registry.ITEM, new Identifier(MODID, name+"item"), itemid);
    }
    public static void regBlock(String name, Block blockid, String MODID){
        Registry.register(Registry.BLOCK, new Identifier(MODID, name+"block"), blockid);
    }
}